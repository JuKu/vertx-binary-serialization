package com.jukusoft.vertx.connection.utils;

import org.junit.Test;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class SocketUtilsTest {

    @Test
    public void testConstructor () {
        new SocketUtils();
    }

    @Test
    public void testCheckRemoteTCPPort () throws IOException {
        assertEquals(false, SocketUtils.checkRemoteTCPPort("127.0.0.1", 10000, 500));
    }

    /*@Test
    public void testCheckRemoteTCPPort1 () throws IOException {
        assertEquals(true, SocketUtils.checkRemoteTCPPort("jukusoft.com", 80, 500));
    }*/

    @Test
    public void testListOwnIPs () throws SocketException, UnknownHostException {
        assertEquals(true, SocketUtils.listOwnIPs().size() > 0);
    }

}
