package com.jukusoft.vertx.connection.clientserver;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.vertx.connection.stream.BufferStream;
import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.util.Objects;

public class ClientConnectionImpl implements RemoteConnection {

    protected NetSocket socket = null;
    protected BufferStream bufferStream = null;
    protected Server server = null;

    //channel attributes, like login state and so on
    protected ObjectObjectMap<String,Object> attributes = new ObjectObjectHashMap<>();

    protected MessageHandler<SerializableObject,RemoteConnection> handler = null;
    protected MessageHandler<Buffer,RemoteConnection> rawHandler = null;

    protected Handler<RemoteConnection> closeHandler = null;

    public ClientConnectionImpl (NetSocket socket, BufferStream bufferStream, Server server) {
        this.socket = socket;
        this.bufferStream = bufferStream;
        this.server = server;
    }

    protected ClientConnectionImpl () {
        //only for testing purposes
    }

    @Override
    public void send(SerializableObject msg) {
        Objects.requireNonNull(msg);

        //serialize object
        Buffer buffer = Serializer.serialize(msg);

        this.sendRaw(buffer);
    }

    @Override
    public void sendRaw(Buffer content) {
        this.bufferStream.write(content);
    }

    @Override
    public ObjectObjectMap<String, Object> attributes() {
        return this.attributes;
    }

    @Override
    public <V> V getAttribute(String key, Class<V> cls) {
        Object obj = this.attributes.get(key);

        if (obj == null) {
            //attribute key doesn't exists
            return null;
        }

        return cls.cast(obj);
    }

    @Override
    public void putAttribute(String key, Object obj) {
        this.attributes.put(key, obj);
    }

    @Override
    public void disconnect() {
        this.handleClose();
    }

    protected void handleMessage (Buffer content) throws Exception {
        if (content == null) {
            throw new NullPointerException("content cannot be null.");
        }

        if (content.length() < 1) {
            throw new IllegalArgumentException("content cannot be empty.");
        }

        if (this.rawHandler != null) {
            this.rawHandler.handle(content, this);
            return;
        }

        if (this.handler != null) {
            this.handler.handle(Serializer.unserialize(content), this);
            return;
        }

        throw new IllegalStateException("No handler was set!");
    }

    protected void handleClose () {
        if (this.closeHandler != null) {
            this.closeHandler.handle(this);
        }

        this.socket.close();
    }

    public String getIP () {
        return socket.remoteAddress().host();
    }

    public int getPort () {
        return socket.remoteAddress().port();
    }

    @Override
    public void setRawHandler (MessageHandler<Buffer,RemoteConnection> rawHandler) {
        this.rawHandler = rawHandler;
    }

    @Override
    public void setCloseHandler (Handler<RemoteConnection> closeHandler) {
        this.closeHandler = closeHandler;
    }

    @Override
    public void setMessageHandler (MessageHandler<SerializableObject,RemoteConnection> handler) {
        this.handler = handler;
    }

}
