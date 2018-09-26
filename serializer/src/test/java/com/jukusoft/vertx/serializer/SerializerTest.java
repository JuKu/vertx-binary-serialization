package com.jukusoft.vertx.serializer;

import com.jukusoft.vertx.serializer.test.TestObject;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializerTest {

    @Test
    public void testConstructor () {
        new Serializer();
    }

    @Test
    public void testSerializer () {
        TestObject obj = new TestObject();

        Buffer buffer = Serializer.serialize(obj);
        TestObject obj1 = Serializer.unserialize(buffer, TestObject.class);

        assertEquals(obj.getClass(), obj1.getClass());
    }

}
