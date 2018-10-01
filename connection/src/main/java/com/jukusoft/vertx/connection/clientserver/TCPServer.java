package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.connection.stream.BufferStream;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPServer implements Server {

    //vert.x variables
    protected VertxOptions vertxOptions = new VertxOptions();
    protected Vertx vertx = null;
    protected NetServerOptions options = new NetServerOptions();

    //options
    protected int eventThreads = 2;
    protected int workerThreads = 2;
    protected boolean logsEnabled = true;
    protected int nOfNetServerThreads = 1;

    protected String host = "0.0.0.0";
    protected int port = 5123;

    //initialization flag
    protected AtomicBoolean initialized = new AtomicBoolean(false);

    //if custom handler is set, messages weren't unserialized, handler is called directly
    protected MessageHandler<Buffer,RemoteConnection> customHandler = null;

    //handler which is called, if a client connects to this server
    protected Handler<RemoteConnection> clientHandler = null;

    //instance of vert.x tcp servers
    protected List<NetServer> servers = new ArrayList<>();

    @Override
    public void init() {
        //set thread count
        vertxOptions.setEventLoopPoolSize(eventThreads);
        vertxOptions.setWorkerPoolSize(workerThreads);

        //set thread pool timeouts
        vertxOptions.setMaxEventLoopExecuteTime(5000);
        vertxOptions.setMaxWorkerExecuteTime(5000);

        //create new NetClient
        this.vertx = Vertx.vertx(vertxOptions);

        this.init(this.vertx);
    }

    @Override
    public void init(Vertx vertx) {
        this.vertx = vertx;
        initialized.set(true);
    }

    @Override
    public void start(int port, Handler<AsyncResult<Server>> listenHandler) {
        this.start("0.0.0.0", port, listenHandler);
    }

    @Override
    public void start(String host, int port, Handler<AsyncResult<Server>> listenHandler) {
        if (!initialized.get()) {
            throw new IllegalStateException("initialize server with TCPServer.init() first!");
        }

        if (this.clientHandler == null) {
            throw new IllegalStateException("No client handler was set. Set handler with TCPServer.setClientHandler() first!");
        }

        this.host = host;
        this.port = port;

        //set host and port
        this.options.setHost(host);
        this.options.setPort(port);

        //Scaling - sharing TCP servers, see https://vertx.io/docs/vertx-core/java/#_scaling_sharing_tcp_servers
        for (int i = 0; i < this.nOfNetServerThreads; i++) {
            //create new tcp server
            NetServer server = this.vertx.createNetServer(options);

            //set connect handler
            server.connectHandler(this::connectHandler);

            //start server
            server.listen(port, host);

            servers.add(server);
        }

        listenHandler.handle(Future.succeededFuture(this));
    }

    protected void connectHandler (NetSocket socket) {
        //create buffer stream
        BufferStream bufferStream = new BufferStream(socket, socket);

        //pause reading data
        bufferStream.pause();

        //TODO: check ip blacklist / firewall

        final ClientConnectionImpl conn = new ClientConnectionImpl(socket, bufferStream, this);

        bufferStream.handler(buffer -> this.messageReceived(buffer, conn));

        bufferStream.endHandler(v -> conn.handleClose());

        //call client handler
        this.clientHandler.handle(conn);

        //resume reading data
        bufferStream.resume();
    }

    protected void messageReceived (Buffer buffer, ClientConnectionImpl conn) {
        try {
            if (customHandler == null) {
                conn.handleMessage(buffer);
            } else {
                customHandler.handle(buffer, conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setThreadPoolSize(int eventThreads, int workerThreads) {
        if (this.initialized.get()) {
            throw new IllegalStateException("You have to call this method before TCPServer was initialized with init() call!");
        }

        this.eventThreads = eventThreads;
        this.workerThreads = workerThreads;
    }

    @Override
    public void setCustomHandler(MessageHandler<Buffer,RemoteConnection> customHandler) {
        if (initialized.get()) {
            throw new IllegalStateException("You cannot set custom message handler if server was already initialized. Call setCustomHandler() before call TCPServer.init()!");
        }

        this.customHandler = customHandler;
    }

    @Override
    public NetServerOptions getNetServerOptions() {
        return this.options;
    }

    @Override
    public void setClientHandler(Handler<RemoteConnection> clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void setServersCount(int nOfServerThreads) {
        this.nOfNetServerThreads = nOfServerThreads;
    }

    @Override
    public void shutdown() {
        for (NetServer server : servers) {
            //shutdown tcp server
            server.close();
        }

        if (this.vertx != null) {
            this.vertx.close();
        }
    }

}
