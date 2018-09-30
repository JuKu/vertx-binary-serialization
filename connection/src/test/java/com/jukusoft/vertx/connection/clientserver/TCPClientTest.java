package com.jukusoft.vertx.connection.clientserver;

import org.junit.Test;

public class TCPClientTest {

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
        client.init();
        client.setThreadPoolSize(2, 2);

        client.shutdown();
    }

}
