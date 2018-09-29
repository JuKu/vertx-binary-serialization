package com.jukusoft.vertx.serializer;

import com.jukusoft.vertx.serializer.exceptions.NoProtocolVersionException;
import com.jukusoft.vertx.serializer.test.TestObject;
import com.jukusoft.vertx.serializer.test.TestObjectWithoutVersion;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializerTest {

    @Test
    public void testConstructor () {
        new Serializer();
    }

    @Test (expected = NoProtocolVersionException.class)
    public void testSerializerWithoutProtocolVersion () {
        TestObjectWithoutVersion obj = new TestObjectWithoutVersion();

        Buffer buffer = Serializer.serialize(obj);
    }

    @Test
    public void testSerializer () {
        TestObject obj = new TestObject();

        long startTime = System.currentTimeMillis();

        Buffer buffer = Serializer.serialize(obj);
        TestObject obj1 = Serializer.unserialize(buffer, TestObject.class);

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] time needed for serialization of one object: " + timeDiff + "ms");

        assertEquals(obj.getClass(), obj1.getClass());
    }

    @Test
    public void testSerialize1000Objects () {
        TestObject obj = new TestObject();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            Buffer buffer = Serializer.serialize(obj);
            TestObject obj1 = Serializer.unserialize(buffer, TestObject.class);
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] time needed for serialization of 1000 objects: " + timeDiff + "ms");
    }

    @Test
    public void testSerializeAndUnserialize () {
        TestObject obj = new TestObject();
        obj.test = 20;
        obj.testStr = "test2";

        long startTime = System.currentTimeMillis();

        Buffer buffer = Serializer.serialize(obj);
        TestObject obj1 = Serializer.unserialize(buffer, TestObject.class);

        assertEquals(obj.test, obj1.test);
        assertEquals(obj.testStr, obj1.testStr);

        System.out.println("serialized int: " + obj.test);
        System.out.println("serialized string: " + obj.testStr);
        System.out.println("serialized int: " + obj1.test);
        System.out.println("serialized string: " + obj1.testStr);

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] time needed for serialization & unserialization of one object: " + timeDiff + "ms");

        assertEquals(obj.getClass(), obj1.getClass());
    }

}
