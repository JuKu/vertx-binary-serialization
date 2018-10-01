package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.serializer.TypeLookup;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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

}
