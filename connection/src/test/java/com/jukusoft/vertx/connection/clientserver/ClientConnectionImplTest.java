package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.connection.stream.BufferStream;
import com.jukusoft.vertx.serializer.test.TestObject;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;

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

}
