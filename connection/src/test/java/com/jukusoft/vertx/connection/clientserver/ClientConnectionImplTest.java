package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.connection.stream.BufferStream;
import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.Serializer;
import com.jukusoft.vertx.serializer.TypeLookup;
import com.jukusoft.vertx.serializer.test.TestObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import org.junit.Test;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ClientConnectionImplTest {

    @Test
    public void testConstructor () {
        new ClientConnectionImpl();
    }

    @Test
    public void testSend () {
        ClientConnectionImpl conn = new ClientConnectionImpl();

        AtomicBoolean b = new AtomicBoolean(false);

        conn.bufferStream = new BufferStream() {
            @Override
            public BufferStream write(Buffer content) {
                b.set(true);

                return this;
            }
        };
        conn.send(new TestObject());

        //check, if write() was called
        assertEquals(true, b.get());
    }

    @Test
    public void testAttributes () {
        ClientConnectionImpl conn = new ClientConnectionImpl();

        //test attributes
        assertNotNull(conn.attributes());
        assertNull(conn.getAttribute("test", String.class));
        conn.putAttribute("test", "test");
        assertNotNull(conn.getAttribute("test", String.class));
    }

    @Test
    public void testGetIpAndPort () {
        ClientConnectionImpl conn = new ClientConnectionImpl();
        conn.socket = createNetSocket();

        assertEquals("127.0.0.1", conn.getIP());
        assertEquals(51, conn.getPort());
    }

    @Test (expected = NullPointerException.class)
    public void testHandleNullMessage () throws Exception {
        ClientConnectionImpl conn = new ClientConnectionImpl();
        conn.socket = createNetSocket();

        conn.handleMessage(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testHandleEmptyMessage () throws Exception {
        ClientConnectionImpl conn = new ClientConnectionImpl();
        conn.socket = createNetSocket();

        conn.handleMessage(Buffer.buffer());
    }

    @Test (expected = IllegalStateException.class)
    public void testHandleMessageWithoutRegisteredHandler () throws Exception {
        ClientConnectionImpl conn = new ClientConnectionImpl();
        conn.socket = createNetSocket();

        conn.handleMessage(Buffer.buffer().appendFloat(1f));
    }

    @Test
    public void testHandleMessage () throws Exception {
        ClientConnectionImpl conn = new ClientConnectionImpl();
        conn.socket = createNetSocket();

        AtomicBoolean b = new AtomicBoolean(false);

        conn.setMessageHandler((msg, conn1) -> {
            b.set(true);
        });

        TypeLookup.register(TestObject.class);
        conn.handleMessage(Serializer.serialize(new TestObject()));

        //check, if handler was called
        assertEquals(true, b.get());

        TypeLookup.removeAll();
    }

    @Test
    public void testDisconnect () {
        ClientConnectionImpl conn = new ClientConnectionImpl();
        conn.socket = createNetSocket();

        conn.disconnect();
    }

    @Test
    public void testDisconnectHandler () {
        ClientConnectionImpl conn = new ClientConnectionImpl();
        conn.socket = createNetSocket();

        AtomicBoolean b = new AtomicBoolean(false);

        conn.setCloseHandler(conn1 -> {
            b.set(true);
        });

        conn.disconnect();

        assertEquals(true, b.get());
    }

    protected static NetSocket createNetSocket () {
        return new NetSocket() {
            @Override
            public NetSocket exceptionHandler(Handler<Throwable> handler) {
                return null;
            }

            @Override
            public NetSocket handler(Handler<Buffer> handler) {
                return null;
            }

            @Override
            public NetSocket pause() {
                return null;
            }

            @Override
            public NetSocket resume() {
                return null;
            }

            @Override
            public NetSocket endHandler(Handler<Void> endHandler) {
                return null;
            }

            @Override
            public NetSocket write(Buffer data) {
                return null;
            }

            @Override
            public NetSocket setWriteQueueMaxSize(int maxSize) {
                return null;
            }

            @Override
            public NetSocket drainHandler(Handler<Void> handler) {
                return null;
            }

            @Override
            public String writeHandlerID() {
                return null;
            }

            @Override
            public NetSocket write(String str) {
                return null;
            }

            @Override
            public NetSocket write(String str, String enc) {
                return null;
            }

            @Override
            public NetSocket sendFile(String filename, long offset, long length) {
                return null;
            }

            @Override
            public NetSocket sendFile(String filename, long offset, long length, Handler<AsyncResult<Void>> resultHandler) {
                return null;
            }

            @Override
            public SocketAddress remoteAddress() {
                return new SocketAddress() {
                    @Override
                    public String host() {
                        return "127.0.0.1";
                    }

                    @Override
                    public int port() {
                        return 51;
                    }

                    @Override
                    public String path() {
                        return null;
                    }
                };
            }

            @Override
            public SocketAddress localAddress() {
                return null;
            }

            @Override
            public void end() {

            }

            @Override
            public void close() {

            }

            @Override
            public NetSocket closeHandler(Handler<Void> handler) {
                return null;
            }

            @Override
            public NetSocket upgradeToSsl(Handler<Void> handler) {
                return null;
            }

            @Override
            public NetSocket upgradeToSsl(String serverName, Handler<Void> handler) {
                return null;
            }

            @Override
            public boolean isSsl() {
                return false;
            }

            @Override
            public SSLSession sslSession() {
                return null;
            }

            @Override
            public X509Certificate[] peerCertificateChain() throws SSLPeerUnverifiedException {
                return new X509Certificate[0];
            }

            @Override
            public String indicatedServerName() {
                return null;
            }

            @Override
            public boolean writeQueueFull() {
                return false;
            }
        };
    }

}
