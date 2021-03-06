package com.jukusoft.vertx.connection.clientserver;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.utils.ByteUtils;

public class HandlerManagerImpl<K extends RemoteConnection> implements HandlerManager<K> {

    protected IntObjectMap<MessageHandler> handlerMap = new IntObjectHashMap<>();

    /**
    * find handler for specific message
     *
     * @param cls class name of message
     *
     * @return handler or null, if no handler is registered
    */
    @Override
    public <T extends SerializableObject> MessageHandler<T,K> findHandler (Class<T> cls) {
        //get type
        MessageType type = getMessageTypeAnnotation(cls);

        //convert 2 bytes to an integer
        int i = ByteUtils.twoBytesToInt(type.type(), type.extendedType());

        return handlerMap.get(i);
    }

    @Override
    public <T extends SerializableObject> void register (Class<T> cls, MessageHandler<T,K> handler) {
        //get type
        MessageType type = getMessageTypeAnnotation(cls);

        //convert 2 bytes to an integer
        int i = ByteUtils.twoBytesToInt(type.type(), type.extendedType());

        this.handlerMap.put(i, handler);
    }

    @Override
    public <T extends SerializableObject> void unregister (Class<T> cls) {
        //get type
        MessageType type = getMessageTypeAnnotation(cls);

        //convert 2 bytes to an integer
        int i = ByteUtils.twoBytesToInt(type.type(), type.extendedType());

        this.handlerMap.remove(i);
    }

    protected <T extends SerializableObject> MessageType getMessageTypeAnnotation (Class<T> cls) {
        //get type
        MessageType type = cls.getAnnotation(MessageType.class);

        if (type == null) {
            throw new IllegalStateException("class '" + cls.getCanonicalName() + "' doesnt contains required annotation @MessageType!");
        }

        return type;
    }

}
