package com.jukusoft.vertx.connection.clientserver;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServerOptions;

public interface Server {

    public void init ();

    public void init (Vertx vertx);

    public void start (int port, Handler<AsyncResult<Server>> listenHandler);

    public void start (String host, int port, Handler<AsyncResult<Server>> listenHandler);

    public void setThreadPoolSize (int eventThreads, int workerThreads);

    public void setCustomHandler (MessageHandler<Buffer,RemoteConnection> customHandler);

    public NetServerOptions getNetServerOptions ();

    /**
    * set handler which is called, if new client connects to server
    */
    public void setClientHandler (Handler<RemoteConnection> clientHandler);

    public void setServersCount (int nOfServerThreads);

    public void shutdown ();

}
