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
        client.init();

        client.shutdown();
    }

}
