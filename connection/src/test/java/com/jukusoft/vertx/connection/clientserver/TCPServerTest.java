package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.serializer.TypeLookup;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    @Test
    public void testSetThreadPoolSize () {
        Server server = new TCPServer();
        server.setThreadPoolSize(2, 2);
        server.init(vertx);
    }

    @Test (expected = IllegalStateException.class)
    public void testSetCustomHandlerWithAlreadyInitializedServer () {
        Server server = new TCPServer();
        server.init(vertx);
        server.setCustomHandler((msg, conn) -> {
            //do something
        });
    }

    @Test
    public void testSetCustomHandler () {
        Server server = new TCPServer();
        server.setCustomHandler((msg, conn) -> {
            //do something
        });
        server.init(vertx);

        assertNotNull(server.getNetServerOptions());
        assertNotNull(((TCPServer) server).customHandler);
    }

    @Test
    public void testConnectHandler () {
        Server server = new TCPServer();
        server.setClientHandler(conn -> {
            //do something
        });
        
        server.init(vertx);

        NetSocket netSocket = new NetSocket() {
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
                return null;
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
        ((TCPServer) server).connectHandler(netSocket);
    }

    @Test
    public void testShutdown () {
        Server server = new TCPServer();
        ((TCPServer) server).vertx = null;
        server.shutdown();
    }

}
