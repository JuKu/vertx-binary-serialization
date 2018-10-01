package com.jukusoft.vertx.serializer;

import com.jukusoft.vertx.serializer.exceptions.NoMessageTypeException;
import com.jukusoft.vertx.serializer.exceptions.NoProtocolVersionException;
import com.jukusoft.vertx.serializer.exceptions.SerializerException;
import com.jukusoft.vertx.serializer.exceptions.UnsupportedProtocolVersionException;
import com.jukusoft.vertx.serializer.test.*;
import com.jukusoft.vertx.serializer.utils.ByteUtils;
import io.vertx.core.buffer.Buffer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

    @Test (expected = NoProtocolVersionException.class)
    public void testUnserializerWithoutProtocolVersion () {
        Buffer buffer = Buffer.buffer();
        Serializer.unserialize(buffer, TestObjectWithoutVersion.class, 2);
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

        assertEquals(false, obj.isTestBool());

        obj.setTestBool(true);
        obj.setTestChar('a');
        obj.b = 0x20;
        obj.bytes = new byte[]{0x01, 0x02};
        obj.shortValue = 200;
        obj.longValue = 50;
        obj.floatValue = 20.1f;
        obj.doubleValue = 1.2d;

        long startTime = System.currentTimeMillis();

        Buffer buffer = Serializer.serialize(obj);
        //System.err.println("serialized buffer: " + buffer.toString());
        //System.err.println("serialized buffer hex: " + ByteUtils.bytesToHex(buffer.getBytes(0, buffer.length())));
        TestObject obj1 = Serializer.unserialize(buffer, TestObject.class);

        assertEquals(obj.test, obj1.test);
        assertEquals(obj.testStr, obj1.testStr);
        assertEquals(true, obj1.isTestBool());
        assertEquals('a', obj1.getTestChar());
        assertEquals(0x20, obj1.b);
        assertEquals(0x01, obj1.bytes[0]);
        assertEquals(0x02, obj1.bytes[1]);
        assertEquals(200, obj1.shortValue);
        assertEquals(50, obj1.longValue);
        assertEquals(20.1f, obj1.floatValue, 0.0001);
        assertEquals(1.2d, obj1.doubleValue, 0.0001);

        System.out.println("serialized int: " + obj.test);
        System.out.println("serialized string: " + obj.testStr);
        System.out.println("serialized int: " + obj1.test);
        System.out.println("serialized string: " + obj1.testStr);

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] time needed for serialization & unserialization of one object: " + timeDiff + "ms");

        //test serializing buffers (because of large memory allocation we do this seperately)
        TypeLookup.register(TestObjectWithBuffer.class);
        Buffer buf1 = Serializer.serialize(new TestObjectWithBuffer());
        Serializer.unserialize(buf1);

        TypeLookup.removeAll();

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

    @Test (expected = NoMessageTypeException.class)
    public void testSerializeWithoutType () {
        Serializer.serialize(new TestObjectWithoutType());
    }

    @Test (expected = IllegalStateException.class)
    public void testSerializeWithNullType () {
        Serializer.serialize(new TestObject3());
    }

    @Test (expected = SerializerException.class)
    public void testUnserializeWithPrivateConstructor () {
        Buffer buffer = Buffer.buffer();

        Serializer.unserialize(buffer, TestObjectWithPrivateConstructor.class, 2);
    }

    @Test (expected = SerializerException.class)
    public void testUnserializeWithAbstractClass () {
        Buffer buffer = Buffer.buffer();

        Serializer.unserialize(buffer, AbstractTestObject.class, 2);
    }

    @Test
    public void testUnserializeWithPrivateVariable () {
        Buffer buffer = Serializer.serialize(new TestObjectWithPrivateVariable());
        Serializer.unserialize(buffer, TestObjectWithPrivateVariable.class, 2);
    }

    @Test (expected = SerializerException.class)
    public void testUnserializeWithFinalVariable () {
        TestObjectWithFinalVariable obj = new TestObjectWithFinalVariable();

        Buffer buffer = Serializer.serialize(obj);
        Serializer.unserialize(buffer, TestObjectWithFinalVariable.class, 2);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUnserializeWithNullPos () {
        Buffer buffer = Serializer.serialize(new TestObject());
        Serializer.unserialize(buffer, TestObjectWithFinalVariable.class, 0);
    }

    @Test
    public void testUnserializeDummyAnnotation () {
        Buffer buffer = Serializer.serialize(new TestObject1());
        Serializer.unserialize(buffer, TestObject1.class, 2);
    }

    @Test (expected = UnsupportedProtocolVersionException.class)
    public void testUnserializeincompatibleVersions () {
        Buffer buffer = Serializer.serialize(new TestObject());
        Serializer.unserialize(buffer, TestObjectV1.class, 2);
    }

    @Test (expected = IllegalStateException.class)
    public void testUnserializeUnregisteredClass () {
        TypeLookup.removeAll();

        Buffer buffer = Serializer.serialize(new TestObject());
        Serializer.unserialize(buffer);
    }

    @Test (expected = NullPointerException.class)
    public void testSerializeNullBytes () {
        Serializer.serialize(new TestObjectWithNullBytes());
    }

    @Test (expected = NullPointerException.class)
    public void testSerializeNullBuffer () {
        Serializer.serialize(new TestObjectWithNullBuffer());
    }

}
