package com.jukusoft.vertx.connection.clientserver;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testConnectUnavailableServer () {
        //
    }

}
