package com.jukusoft.vertx.connection.clientserver;

public interface MessageHandler<T,K extends RemoteConnection> {

    /**
    * handle received message
    */
    public void handle (T msg, K conn) throws Exception;

}
