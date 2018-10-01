package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.connection.exception.NoHandlerException;
import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.Serializer;
import com.jukusoft.vertx.serializer.TypeLookup;
import com.jukusoft.vertx.serializer.exceptions.NetworkException;
import com.jukusoft.vertx.serializer.test.TestObject;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    }

    @Test (expected = NoHandlerException.class)
    public void testHandleMessageWithoutRegisteredHandler () {
        Client client = new TCPClient();
        TypeLookup.register(TestObject.class);
        ((TCPClient) client).handleMessage(Serializer.serialize(new TestObject()));
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
    }

    @Test (timeout = 10000)
    public void testConnectServer () throws InterruptedException {
        //start test server
        mockServer = startClientAndServer(1080);

        Client client = new TCPClient();
        client.init();

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

        TypeLookup.register(TestObject.class);

        //try to send something
        client.send(new TestObject());

        //set delay
        client.setDelay(10, 10);

        //send with delay
        client.send(new TestObject());

        //send with RemoteConnection instance
        ((TCPClient) client).conn.send(new TestObject());

        //send raw buffer
        ((TCPClient) client).conn.sendRaw(Buffer.buffer());

        //receive message with delay
        ((TCPClient) client).handleMessageWithDelay(Serializer.serialize(new TestObject()));

        assertNotNull(((TCPClient) client).getVertxClient());

        //test attributes
        assertNotNull(((TCPClient) client).conn.attributes());
        assertNull(((TCPClient) client).conn.getAttribute("test", String.class));
        ((TCPClient) client).conn.putAttribute("test", "test");
        assertNotNull(((TCPClient) client).conn.getAttribute("test", String.class));

        client.disconnect();

        client.shutdown();

        //stop test server
        mockServer.stop();

        TypeLookup.removeAll();
    }

    @Test (timeout = 10000)
    public void testConnectServerList () throws InterruptedException {
        //start test server
        mockServer = startClientAndServer(1080);

        Client client = new TCPClient();
        client.init();

        //assertEquals(((TCPClient) client).getVertxClient().);

        final AtomicBoolean b = new AtomicBoolean(false);
        final AtomicReference<AsyncResult<RemoteConnection>> res1 = new AtomicReference<>();

        List<ServerData> serverList = new ArrayList<>();
        serverList.add(new ServerData("127.0.0.1", 50));
        serverList.add(new ServerData("127.0.0.1", 1080));

        client.connect(serverList, res -> {
            System.out.println("connection state.");

            res1.set(res);
            b.set(true);
        });

        while (!b.get()) {
            //wait for result
            Thread.currentThread().sleep(50);
        }

        assertEquals(true, res1.get().succeeded());

        TypeLookup.register(TestObject.class);

        //try to send something
        client.send(new TestObject());

        client.disconnect();

        client.shutdown();

        //stop test server
        mockServer.stop();

        TypeLookup.removeAll();
    }

    @Test (expected = IllegalArgumentException.class, timeout = 5000)
    public void testConnectEmptyServerList () throws InterruptedException {
        Client client = new TCPClient();
        ((TCPClient) client).initialized.set(true);

        //assertEquals(((TCPClient) client).getVertxClient().);

        final AtomicBoolean b = new AtomicBoolean(false);
        final AtomicReference<AsyncResult<RemoteConnection>> res1 = new AtomicReference<>();

        List<ServerData> serverList = new ArrayList<>();

        client.connect(serverList, res -> {
            System.out.println("connection state.");

            res1.set(res);
            b.set(true);
        });

        client.shutdown();

        TypeLookup.removeAll();
    }

    @Test (expected = NetworkException.class, timeout = 5000)
    public void testConnectServerListAllServersDown () throws InterruptedException {
        Client client = new TCPClient();
        client.init();

        //assertEquals(((TCPClient) client).getVertxClient().);

        final AtomicBoolean b = new AtomicBoolean(false);
        final AtomicReference<AsyncResult<RemoteConnection>> res1 = new AtomicReference<>();

        List<ServerData> serverList = new ArrayList<>();
        serverList.add(new ServerData("127.0.0.1", 1080));
        serverList.add(new ServerData("127.0.0.1", 50));
        serverList.add(new ServerData("127.0.0.1", 89));

        client.connect(serverList, res -> {
            System.out.println("connection state.");

            res1.set(res);
            b.set(true);
        });

        while (!b.get()) {
            //wait for result
            Thread.currentThread().sleep(50);
        }

        assertEquals(true, res1.get().succeeded());

        TypeLookup.register(TestObject.class);

        //try to send something
        client.send(new TestObject());

        client.disconnect();

        client.shutdown();

        TypeLookup.removeAll();
    }

    @Test
    public void testCreateRemoteConnection () {
        Client client = new TCPClient();
        client.init(vertx);

        ((TCPClient) client).createRemoteConnection();
    }

    @Test (expected = IllegalStateException.class)
    public void testCreateRemoteConnection1 () {
        Client client = new TCPClient();
        client.init(vertx);

        ((TCPClient) client).createRemoteConnection();
        ((TCPClient) client).conn.send(new TestObject());
    }

    @Test
    public void testCreateRemoteConnection2 () {
        Client client = new TCPClient();
        client.init(vertx);

        ((TCPClient) client).createRemoteConnection();
        ((TCPClient) client).conn.disconnect();
    }

    @Test (expected = IllegalStateException.class)
    public void testCreateRemoteConnection3 () {
        Client client = new TCPClient();
        client.init(vertx);

        AtomicBoolean b = new AtomicBoolean(false);

        TypeLookup.register(TestObject.class);
        client.handlers().register(TestObject.class, (msg, conn) -> {
            b.set(true);
        });

        ((TCPClient) client).createRemoteConnection();

        //this line should throw an IllegalStateException, because no connection is established yet.
        ((TCPClient) client).conn.send(new TestObject());

        TypeLookup.removeAll();

        //check, if handler was called
        assertEquals(true, b.get());
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testCreateRemoteConnection4 () {
        Client client = new TCPClient();
        client.init(vertx);

        ((TCPClient) client).createRemoteConnection();
        ((TCPClient) client).conn.setRawHandler(null);
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testCreateRemoteConnection5 () {
        Client client = new TCPClient();
        client.init(vertx);

        ((TCPClient) client).createRemoteConnection();
        ((TCPClient) client).conn.setCloseHandler(null);
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testCreateRemoteConnection6 () {
        Client client = new TCPClient();
        client.init(vertx);

        ((TCPClient) client).createRemoteConnection();
        ((TCPClient) client).conn.setMessageHandler(null);
    }

    @Test
    public void testConnectHandlerFailed () {
        TCPClient client = new TCPClient();
        AtomicBoolean b = new AtomicBoolean(false);

        Handler<AsyncResult<RemoteConnection>> handler = res -> {
            b.set(res.succeeded());
        };

        client.connectHandler(null, Future.failedFuture("connection failed"), handler);

        assertEquals(false, b.get());
    }

    @Test
    public void testHandleMessageWithDelay () {
        TCPClient client = new TCPClient();
        client.init(vertx);

        AtomicBoolean b = new AtomicBoolean(false);

        TypeLookup.register(TestObject.class);
        client.handlers().register(TestObject.class, (msg, conn) -> {
            b.set(true);
        });

        client.handleMessageWithDelay(Serializer.serialize(new TestObject()));

        TypeLookup.removeAll();

        //check, if handler was called
        assertEquals(true, b.get());
    }

    /*@Test
    public void testHandleMessageWithDelay1 () {
        TCPClient client = new TCPClient();
        client.setDelay(10, 10);
        client.init(vertx);

        AtomicBoolean b = new AtomicBoolean(false);

        TypeLookup.register(TestObject.class);
        client.handlers().register(TestObject.class, (msg, conn) -> {
            b.set(true);
        });

        client.handleMessageWithDelay(Serializer.serialize(new TestObject()));

        TypeLookup.removeAll();

        //check, if handler was called
        assertEquals(true, b.get());
    }*/

    @Test
    public void testHandleMessageWithExceptionInHandler () {
        TCPClient client = new TCPClient();
        client.init(vertx);

        AtomicBoolean b = new AtomicBoolean(false);

        TypeLookup.register(TestObject.class);
        client.handlers().register(TestObject.class, (msg, conn) -> {
            b.set(true);

            throw new Exception("test exception.");
        });

        client.handleMessageWithDelay(Serializer.serialize(new TestObject()));

        TypeLookup.removeAll();

        //check, if handler was called
        assertEquals(true, b.get());
    }

    @Test
    public void testAttributes () {
        //
    }

    @Test
    public void testDisconnect () {
        Client client = new TCPClient();
        client.init(vertx);
        client.disconnect();
    }

    @Test
    public void testShutdown () {
        Client client = new TCPClient();
        client.shutdown();
    }

    @Test
    public void testShutdown1 () {
        Client client = new TCPClient();
        ((TCPClient) client).initialized.set(true);
        ((TCPClient) client).vertx = null;

        client.shutdown();
    }

    @Test
    public void testShutdown2 () {
        Client client = new TCPClient();
        client.init();
        ((TCPClient) client).initialized.set(false);

        client.shutdown();
    }

}
