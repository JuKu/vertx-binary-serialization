package com.jukusoft.vertx.connection.clientserver;

import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.vertx.serializer.SerializableObject;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;

public interface RemoteConnection {

    /**
    * send message to remote connection
    */
    public void send (SerializableObject msg);

    /**
     * send message to remote connection
     */
    public void sendRaw (Buffer content);

    public ObjectObjectMap<String,Object> attributes ();

    public <V> V getAttribute (String key, Class<V> cls);

    public void putAttribute (String key, Object value);

    /**
    * disconnect from remote connection
    */
    public void disconnect ();

    public void setRawHandler (MessageHandler<Buffer,RemoteConnection> rawHandler);

    public void setCloseHandler (Handler<RemoteConnection> closeHandler);

    public void setMessageHandler (MessageHandler<SerializableObject,RemoteConnection> handler);

}
