package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.connection.exception.NoHandlerException;
import com.jukusoft.vertx.serializer.Serializer;
import com.jukusoft.vertx.serializer.TypeLookup;
import com.jukusoft.vertx.serializer.exceptions.NetworkException;
import com.jukusoft.vertx.serializer.test.TestObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

public class TCPClientTest {

    private ClientAndServer mockServer;

    protected static VertxOptions vertxOptions = new VertxOptions();
    protected static Vertx vertx = null;

    @BeforeClass
    public static void beforeClass () {
        TypeLookup.removeAll();

        //set thread count
        vertxOptions.setEventLoopPoolSize(2);
        vertxOptions.setWorkerPoolSize(2);

        //set thread pool timeouts
        vertxOptions.setMaxEventLoopExecuteTime(5000);
        vertxOptions.setMaxWorkerExecuteTime(5000);

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
        new TCPClient();
    }

    @Test
    public void testInit () {
        Client client = new TCPClient();
        client.setThreadPoolSize(2, 2);
        client.init();

        client.shutdown();
    }

    @Test (expected = IllegalStateException.class)
    public void testInitWrongOrder () {
        Client client = new TCPClient();
        client.init(vertx);
        client.setThreadPoolSize(2, 2);

        client.shutdown();
    }

    @Test (expected = IllegalStateException.class)
    public void testConnectWithoutInit () {
        Client client = new TCPClient();
        client.connect(new ServerData("127.0.0.1", 80), event -> {
            //do something
        });
    }

    @Test (expected = IllegalStateException.class)
    public void testConnectWithoutInit1 () {
        Client client = new TCPClient();
        List<ServerData> serverList = new ArrayList<>();
        serverList.add(new ServerData("127.0.0.1", 80));

        client.connect(serverList, event -> {
            //do something
        });
    }

    @Test (expected = IllegalStateException.class)
    public void testHandleMessageWithoutRegisteredMessageType () {
        TypeLookup.removeAll();

        Client client = new TCPClient();
        ((TCPClient) client).handleMessage(Serializer.serialize(new TestObject()));

        client.shutdown();
    }

    @Test (expected = NoHandlerException.class)
    public void testHandleMessageWithoutRegisteredHandler () {
        Client client = new TCPClient();
        TypeLookup.register(TestObject.class);
        ((TCPClient) client).handleMessage(Serializer.serialize(new TestObject()));

        client.shutdown();
    }

    @Test (expected = NetworkException.class, timeout = 5000)
    public void testConnectUnavailableServer () {
        Client client = new TCPClient();
        client.init(vertx);

        //assertEquals(((TCPClient) client).getVertxClient().);

        final AtomicBoolean b = new AtomicBoolean(false);
        final AtomicReference<AsyncResult<RemoteConnection>> res1 = new AtomicReference<>();

        client.connect(new ServerData("127.0.0.1", 5123), res -> {
            System.out.println("connection state.");

            res1.set(res);
            b.set(true);
        });

        client.shutdown();
    }

    @Test (timeout = 5000)
    public void testConnectServer () throws InterruptedException {
        //start test server
        mockServer = startClientAndServer(1080);

        Client client = new TCPClient();
        client.init(vertx);

        //assertEquals(((TCPClient) client).getVertxClient().);

        final AtomicBoolean b = new AtomicBoolean(false);
        final AtomicReference<AsyncResult<RemoteConnection>> res1 = new AtomicReference<>();

        client.connect(new ServerData("127.0.0.1", 1080), res -> {
            System.out.println("connection state.");

            res1.set(res);
            b.set(true);
        });

        while (!b.get()) {
            //wait for result
            Thread.currentThread().sleep(50);
        }

        assertEquals(true, res1.get().succeeded());

        //stop test server
        mockServer.stop();

        client.shutdown();
    }

    @Test
    public void testCreateRemoteConnection () {
        Client client = new TCPClient();
        client.init(vertx);

        ((TCPClient) client).createRemoteConnection();

        client.shutdown();
    }

}
