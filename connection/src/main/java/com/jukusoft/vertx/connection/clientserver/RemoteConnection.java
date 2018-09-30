package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.serializer.SerializableObject;

public interface RemoteConnection {

    /**
    * send message to remote connection
    */
    public void send (SerializableObject msg);

    /**
    * disconnect from remote connection
    */
    public void disconnect ();

}
