package com.jukusoft.vertx.connection.clientserver;

import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.vertx.serializer.SerializableObject;

public interface RemoteConnection {

    /**
    * send message to remote connection
    */
    public void send (SerializableObject msg);

    public ObjectObjectMap<String,Object> attributes ();

    public <V> V getAttribute (String key, Class<V> cls);

    public void putAttribute (String key, Object value);

    /**
    * disconnect from remote connection
    */
    public void disconnect ();

}
