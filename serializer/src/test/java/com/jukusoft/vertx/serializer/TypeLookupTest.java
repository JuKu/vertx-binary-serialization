package com.jukusoft.vertx.serializer;

import com.jukusoft.vertx.serializer.test.TestObject2;
import com.jukusoft.vertx.serializer.test.TestObjectWithoutType;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TypeLookupTest {

    @Test
    public void testConstructor () {
        new TypeLookup();
    }

    @Test
    public void testRegisterAndFind () {
        //type isn't registered yet
        assertNull(TypeLookup.find((byte) 0x01, (byte) 0x02));

        //register type
        TypeLookup.register(TestObject2.class);
        assertNotNull(TypeLookup.find((byte) 0x01, (byte) 0x02));

        //unregister type
        TypeLookup.unregister(TestObject2.class);
        assertNull(TypeLookup.find((byte) 0x01, (byte) 0x02));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRegisterWithoutType () {
        TypeLookup.register(TestObjectWithoutType.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUnregisterWithoutType () {
        TypeLookup.unregister(TestObjectWithoutType.class);
    }

}
