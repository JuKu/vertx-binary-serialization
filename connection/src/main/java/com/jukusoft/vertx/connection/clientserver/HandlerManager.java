package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.serializer.SerializableObject;

public interface HandlerManager<K extends RemoteConnection> {

    /**
     * find handler for specific message
     *
     * @param cls class name of message
     *
     * @return handler or null, if no handler is registered
     */
    public <T extends SerializableObject> MessageHandler<T,K> findHandler (Class<T> cls);

    public <T extends SerializableObject> void register (Class<T> cls, MessageHandler<T,K> handler);

    public <T extends SerializableObject> void unregister (Class<T> cls);

}
