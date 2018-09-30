package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.Serializer;
import com.jukusoft.vertx.serializer.exceptions.NetworkException;
import com.jukusoft.vertx.connection.stream.BufferStream;
import com.jukusoft.vertx.connection.utils.SocketUtils;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPClient implements Client {

    //vert.x variables
    protected VertxOptions vertxOptions = new VertxOptions();
    protected Vertx vertx = null;
    protected NetClientOptions options = new NetClientOptions();
    protected NetClient client = null;

    //options
    protected int sendDelay = 0;
    protected int receiveDelay = 0;
    protected int connectTimeout = 500;
    protected int reconnectAttempts = 10;
    protected int reconnectInterval = 500;
    protected boolean logsEnabled = false;

    //message handler lookup manager
    protected HandlerManager<RemoteConnection> handlerManager = new HandlerManagerImpl<>();

    //buffer stream is responsible for reconstructing fragmented tcp messages to original sized packages
    protected BufferStream bufferStream = null;

    protected AtomicBoolean initialized = new AtomicBoolean(false);

    protected int eventThreads = 2;
    protected int workerThreads = 2;

    protected RemoteConnection conn = null;
    protected NetSocket socket = null;

    @Override
    public void init() {
        this.vertx = Vertx.vertx(vertxOptions);
        this.client = vertx.createNetClient(options);

        //set thread count
        vertxOptions.setEventLoopPoolSize(eventThreads);
        vertxOptions.setWorkerPoolSize(workerThreads);

        //set timeouts
        options.setConnectTimeout(this.connectTimeout);

        //set reconnect options
        options.setReconnectAttempts(this.reconnectAttempts);
        options.setReconnectInterval(this.reconnectInterval);

        //set logging options
        options.setLogActivity(this.logsEnabled);

        this.initialized.set(true);
    }

    @Override
    public void connect(final ServerData server, final Handler<AsyncResult<RemoteConnection>> handler) {
        //try to connect to server
        this.client.connect(server.port, server.ip, res -> this.connectHandler(server, res, handler));
    }

    protected void connectHandler (final ServerData server, final AsyncResult<NetSocket> res, final Handler<AsyncResult<RemoteConnection>> handler) {
        if (res.succeeded()) {
            //connection established

            //get socket
            this.socket = res.result();

            this.bufferStream = new BufferStream(socket, socket);

            //pause reading data
            bufferStream.pause();

            //initialize socket
            this.initSocket(bufferStream);

            //resume reading data
            bufferStream.resume();

            System.out.println("Connected to server " + server.ip + ":" + server.port + " (own port: " + socket.localAddress().port() + ").");

            this.conn = new RemoteConnection() {
                @Override
                public void send(SerializableObject msg) {
                    TCPClient.this.send(msg);
                }

                @Override
                public void disconnect() {
                    TCPClient.this.disconnect();
                }
            };

            //call handler, so UI can be updated
            handler.handle(Future.succeededFuture(this.conn));
        } else {
            //connection failed

        }
    }

    protected void initSocket (BufferStream bufferStream) {
        //set handler
        bufferStream.handler(this::handleMessageWithDelay);
        bufferStream.endHandler(this::onConnectionClosed);
    }

    protected void handleMessageWithDelay (Buffer content) {
        //https://github.com/vert-x3/vertx-examples/blob/master/core-examples/src/main/java/io/vertx/example/core/net/stream/BatchStream.java

        if (this.receiveDelay > 0) {
            //delay message and handle them
            vertx.setTimer(this.receiveDelay, timerID -> handleMessage(content));
        } else {
            //handle message without delay
            handleMessage(content);
        }
    }

    protected void handleMessage (Buffer buf) {
        //first, unserialize object
        SerializableObject msg = Serializer.unserialize(buf);

        //get handler
        MessageHandler handler = this.handlers().findHandler(msg.getClass());

        if (handler == null) {
            throw new IllegalStateException("No handler registered for message class '" + msg.getClass().getCanonicalName() + "'!");
        }

        try {
            handler.handle(msg, this.conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(List<ServerData> serverList, Handler<AsyncResult<RemoteConnection>> handler) {
        if (!initialized.get()) {
            throw new IllegalStateException("TCPClient wasn't initialized before, call TCPClient.init() first!");
        }

        //first, shuffle list
        Collections.shuffle(serverList);

        if (serverList.isEmpty()) {
            throw new IllegalArgumentException("server list is empty or all servers are down!");
        }

        //get first entry
        ServerData server = serverList.get(0);

        //remove server from list, so if server isn't available, client doesn't connects to this server twice
        serverList.remove(server);

        //check, if server is available
        if (!SocketUtils.checkRemoteTCPPort(server.ip, server.port, this.connectTimeout)) {
            //server isn't available
            if (!serverList.isEmpty()) {
                //try another server
                this.connect(serverList, handler);

                return;
            }

            throw new NetworkException("All servers in list are currently down!");
        }

        //connect to one of these servers
        this.connect(server, handler);
    }

    @Override
    public void disconnect() {
        if (this.socket != null) {
            this.socket.close();
        }
    }

    @Override
    public void setDelay(int sendDelay, int receiveDelay) {
        this.sendDelay = sendDelay;
        this.receiveDelay = receiveDelay;
    }

    @Override
    public HandlerManager handlers() {
        return this.handlerManager;
    }

    @Override
    public void send(SerializableObject msg) {
        //serialize message object
        Buffer content = Serializer.serialize(msg);

        if (this.bufferStream == null) {
            throw new IllegalStateException("no connection is established.");
        }

        //if configuration has send delay enable, delay sending message to simulate external server
        if (this.sendDelay > 0) {
            vertx.setTimer(this.sendDelay, timerID -> this.bufferStream.write(content));
        } else {
            this.bufferStream.write(content);
        }
    }

    @Override
    public void setThreadPoolSize(int eventThreads, int workerThreads) {
        if (this.initialized.get()) {
            throw new IllegalStateException("You have to call this method before TCPClient was initialized with init() call!");
        }

        this.eventThreads = eventThreads;
        this.workerThreads = workerThreads;
    }

    @Override
    public void shutdown() {
        this.client.close();
        this.vertx.close();
    }

    protected void onConnectionClosed(Void v) {
        //TODO: call handlers
    }

    public NetClient getVertxClient () {
        return this.client;
    }

}
