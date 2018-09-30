package com.jukusoft.vertx.connection.clientserver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServerDataTest {

    @Test
    public void testConstructor () {
        ServerData server = new ServerData("10.0.0.1", 80);

        assertEquals("10.0.0.1", server.ip);
        assertEquals(80, server.port);
    }

}
