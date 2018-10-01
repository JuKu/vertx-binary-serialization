package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.TypeLookup;
import com.jukusoft.vertx.serializer.test.TestObject;
import com.jukusoft.vertx.serializer.test.TestObject1;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

public class ClientServerTest {

    @Test (timeout = 30000)
    public void testClientServerConnect () throws InterruptedException {
        TypeLookup.removeAll();

        AtomicBoolean a = new AtomicBoolean(false);
        AtomicBoolean b = new AtomicBoolean(false);
        AtomicBoolean newClientHandlerCalled = new AtomicBoolean(false);

        //create and start new tcp server
        Server server = new TCPServer();
        server.setClientHandler(conn -> {
            newClientHandlerCalled.set(true);
        });
        server.init();
        server.start(5123, event -> a.set(true));

        Thread.currentThread().sleep(200);

        //create and start new tcp client
        Client client = new TCPClient();
        client.init();

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

    @Test (timeout = 30000)
    public void testClientServerSendMessage () throws InterruptedException {
        TypeLookup.removeAll();

        AtomicBoolean a = new AtomicBoolean(false);
        AtomicBoolean b = new AtomicBoolean(false);
        AtomicBoolean newClientHandlerCalled = new AtomicBoolean(false);
        AtomicBoolean messageHandlerWasCalled = new AtomicBoolean(false);
        AtomicBoolean clientReceivedMessage = new AtomicBoolean(false);

        AtomicLong messageReceivedTimestamp = new AtomicLong(0);

        //create and start new tcp server
        Server server = new TCPServer();
        server.setClientHandler(conn -> {
            newClientHandlerCalled.set(true);

            conn.setMessageHandler((msg, conn1) -> {
                System.err.println("server conn message handler executed.");

                messageHandlerWasCalled.set(true);
                messageReceivedTimestamp.set(System.currentTimeMillis());
            });

            conn.send(new TestObject1());
        });
        server.init();
        server.start(5123, event -> a.set(true));

        Thread.currentThread().sleep(200);

        //create and start new tcp client
        Client client = new TCPClient();
        client.handlers().register(TestObject1.class, (msg, conn) -> {
            System.err.println("client conn message handler executed.");
            clientReceivedMessage.set(true);
        });
        client.init();

        //connect to server
        client.connect(new ServerData("127.0.0.1", 5123), event -> b.set(true));

        while (!a.get() || !b.get()) {
            System.out.println("a: " + a.get() + ", b: " + b.get());
            Thread.currentThread().sleep(5);
        }

        assertEquals(true, newClientHandlerCalled.get());

        TypeLookup.register(TestObject.class);
        TypeLookup.register(TestObject1.class);

        long startTime = System.currentTimeMillis();

        //send message from client to server
        client.send(new TestObject());

        long endTime = System.currentTimeMillis();
        long diffTime = endTime - startTime;

        System.err.println("[Benchmark] time required for sending TestObject: " + diffTime + "ms");

        Thread.currentThread().sleep(200);

        diffTime = messageReceivedTimestamp.get() - startTime;
        System.err.println("[Benchmark] receiving one TestObject message takes " + diffTime + "ms");

        //check, if client received message
        assertEquals(true, messageHandlerWasCalled.get());

        startTime = System.currentTimeMillis();

        //send message 10 times
        for (int i = 0; i < 10; i++) {
            //send message from client to server
            client.send(new TestObject());
        }

        endTime = System.currentTimeMillis();
        diffTime = endTime - startTime;

        System.err.println("[Benchmark] time required for sending TestObject 10 times: " + diffTime + "ms");

        assertEquals(true, clientReceivedMessage.get());

        client.shutdown();
        server.shutdown();
    }

}
