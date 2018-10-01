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

import static org.junit.Assert.assertEquals;

public class ClientServerTest {

    protected static VertxOptions vertxOptions = new VertxOptions();
    protected static Vertx vertx = null;

    @BeforeClass
    public static void beforeClass () {
        TypeLookup.removeAll();

        //set thread count
        vertxOptions.setEventLoopPoolSize(2);
        vertxOptions.setWorkerPoolSize(2);

        //set thread pool timeouts
        vertxOptions.setMaxEventLoopExecuteTime(10000);
        vertxOptions.setMaxWorkerExecuteTime(10000);

        //create new NetClient
        vertx = Vertx.vertx(vertxOptions);
    }

    @AfterClass
    public static void afterClass () {
        TypeLookup.removeAll();
        vertx.close();
    }

    @Test (timeout = 30000)
    public void testClientServerConnect () throws InterruptedException {
        AtomicBoolean a = new AtomicBoolean(false);
        AtomicBoolean b = new AtomicBoolean(false);
        AtomicBoolean newClientHandlerCalled = new AtomicBoolean(false);

        //create and start new tcp server
        Server server = new TCPServer();
        server.setClientHandler(conn -> {
            newClientHandlerCalled.set(true);
        });
        server.init(vertx);
        server.start(5123, event -> a.set(true));

        //create and start new tcp client
        Client client = new TCPClient();
        client.init(vertx);

        //connect to server
        client.connect(new ServerData("127.0.0.1", 5123), event -> b.set(true));

        while (!a.get() || !b.get()) {
            System.out.println("a: " + a.get() + ", b: " + b.get());
            Thread.currentThread().sleep(5);
        }

        client.shutdown();
        server.shutdown();

        assertEquals(true, newClientHandlerCalled.get());
    }

}
