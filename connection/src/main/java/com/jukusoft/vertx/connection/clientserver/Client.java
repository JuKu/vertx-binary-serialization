package com.jukusoft.vertx.connection.clientserver;

import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.vertx.serializer.SerializableObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;

import java.util.List;

public interface Client {

    public void init ();

    public void init (Vertx vertx);

    /**
    * connect to server
    */
    public void connect (ServerData server, Handler<AsyncResult<RemoteConnection>> handler);

    /**
    * connect to one of these servers
    */
    public void connect (List<ServerData> serverList, Handler<AsyncResult<RemoteConnection>> handler);

    public void disconnect ();

    /**
    * set delay for simulating network lag (if server runs on same host)
     *
     * @param sendDelay delay in milliseconds for sending messages
     * @param receiveDelay delay in milliseconds for receiving messages
    */
    public void setDelay (int sendDelay, int receiveDelay);

    public HandlerManager handlers ();

    public void send (SerializableObject msg);

    public void sendRaw (Buffer msg);

    public ObjectObjectMap<String,Object> attributes ();

    public <V> V getAttribute (String key, Class<V> cls);

    public void putAttribute (String key, Object value);

    public void setThreadPoolSize (int eventThreads, int workerThreads);

    public void setOnConnectionClosedHandler (Runnable runnable);

    /**
     * Set a one-shot timer to fire after {@code delay} milliseconds, at which point {@code handler} will be called with
     * the id of the timer.
     *
     * @param delay  the delay in milliseconds, after which the timer will fire
     * @param handler  the handler that will be called with the timer ID when the timer fires
     * @return the unique ID of the timer
     */
    public long setTimer(long delay, Handler<Long> handler);

    /**
     * Set a periodic timer to fire every {@code delay} milliseconds, at which point {@code handler} will be called with
     * the id of the timer.
     *
     *
     * @param delay  the delay in milliseconds, after which the timer will fire
     * @param handler  the handler that will be called with the timer ID when the timer fires
     * @return the unique ID of the timer
     */
    public long setPeriodic(long delay, Handler<Long> handler);

    /**
    * close connection, but not vert.x
    */
    public void close ();

    /**
    * shutdown vert.x with all thread pools and so on
    */
    public void shutdown ();

}
