package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.serializer.TypeLookup;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class TCPServerTest {

    protected static VertxOptions vertxOptions = new VertxOptions();
    protected static Vertx vertx = null;

    @BeforeClass
    public static void beforeClass () {
        TypeLookup.removeAll();

        //set thread count
        vertxOptions.setEventLoopPoolSize(2);
        vertxOptions.setWorkerPoolSize(2);

        //set thread pool timeouts
        vertxOptions.setMaxEventLoopExecuteTime(3000);
        vertxOptions.setMaxWorkerExecuteTime(3000);

        //create new NetClient
        vertx = Vertx.vertx(vertxOptions);
    }

    @AfterClass
    public static void afterClass () {
        TypeLookup.removeAll();
        vertx.close();
    }

    @Test
    public void testConstructor () {
        new TCPServer();
    }

    @Test
    public void testInit () {
        Server server = new TCPServer();
        server.init();

        server.shutdown();
    }

    @Test (expected = IllegalStateException.class)
    public void testStartWithoutInit () {
        Server server = new TCPServer();
        server.start(51, null);
    }

    @Test (expected = IllegalStateException.class)
    public void testStartWithoutClientHandler () {
        Server server = new TCPServer();
        server.init(vertx);
        server.start(51, null);
    }

    @Test
    public void testStart () {
        Server server = new TCPServer();
        server.init(vertx);

        //set client handler
        server.setClientHandler(event -> {
            //
        });

        server.start(1050, event -> {
            //do something
        });

        server.shutdown();
    }

    @Test
    public void testStart1 () {
        Server server = new TCPServer();
        server.init(vertx);

        //start 2 threads
        server.setServersCount(2);

        //set client handler
        server.setClientHandler(event -> {
            //
        });

        server.start(1050, event -> {
            //do something
        });

        server.shutdown();
    }

    @Test (expected = IllegalStateException.class)
    public void testSetThreadPoolSizeWithAlreadyInitializedServer () {
        Server server = new TCPServer();
        server.init(vertx);
        server.setThreadPoolSize(2, 2);
    }

}
