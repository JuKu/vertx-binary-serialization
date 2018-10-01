package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.serializer.SerializableObject;

public interface MessageHandler<T,K extends RemoteConnection> {

    /**
    * handle received message
    */
    public void handle (T msg, K conn) throws Exception;

}
