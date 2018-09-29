package com.jukusoft.vertx.serializer;

import com.jukusoft.vertx.serializer.exceptions.NoProtocolVersionException;
import com.jukusoft.vertx.serializer.test.TestObject;
import com.jukusoft.vertx.serializer.test.TestObjectWithoutVersion;
import com.jukusoft.vertx.serializer.utils.ByteUtils;
import io.vertx.core.buffer.Buffer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SerializerTest {

    @BeforeClass
    public static void beforeClass () {
        TypeLookup.removeAll();
    }

    @AfterClass
    public static void afterClass () {
        TypeLookup.removeAll();
    }

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
        //System.err.println("serialized buffer: " + buffer.toString());
        //System.err.println("serialized buffer hex: " + ByteUtils.bytesToHex(buffer.getBytes(0, buffer.length())));
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

    @Test
    public void testSerializeAndUnserialize100Times () {
        TestObject obj = new TestObject();
        obj.test = 20;
        obj.testStr = "test2";

        TestObject[] objs = new TestObject[1000000];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < objs.length; i++) {
            Buffer buffer = Serializer.serialize(obj);

            objs[i] = Serializer.unserialize(buffer, TestObject.class);

            assertEquals(20, objs[i].test);
            assertEquals("test2", objs[i].testStr);
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] time needed for serialization & unserialization of 1.000.000 objects: " + timeDiff + "ms");
    }

    @Test
    public void testSerializeAndUnserialize100TimesWithoutClass () {
        TestObject obj = new TestObject();
        obj.test = 20;
        obj.testStr = "test2";

        TestObject[] objs = new TestObject[1000000];

        //register type
        TypeLookup.register(TestObject.class);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < objs.length; i++) {
            Buffer buffer = Serializer.serialize(obj);
            assertNotNull(buffer);

            objs[i] = Serializer.unserialize(buffer);

            assertEquals(20, objs[i].test);
            assertEquals("test2", objs[i].testStr);
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] time needed for serialization & unserialization of 1.000.000 objects: " + timeDiff + "ms");
    }

}
