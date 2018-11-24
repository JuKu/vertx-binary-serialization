package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.connection.stream.BufferStream;

@FunctionalInterface
public interface CustomClientInitializer {

    public void handleConnect (BufferStream bufferStream, RemoteConnection conn);

}
