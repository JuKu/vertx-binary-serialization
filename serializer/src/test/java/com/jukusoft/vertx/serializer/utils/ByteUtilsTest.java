package com.jukusoft.vertx.serializer.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByteUtilsTest {

    @Test
    public void testConstructor () {
        new ByteUtils();
    }

    @Test
    public void testTwoBytesToInt () {
        assertEquals(0, ByteUtils.twoBytesToInt((byte) 0x00, (byte) 0x00));
        assertEquals(1, ByteUtils.twoBytesToInt((byte) 0x00, (byte) 0x01));
        assertEquals(256, ByteUtils.twoBytesToInt((byte) 0x01, (byte) 0x00));
        assertEquals(65535, ByteUtils.twoBytesToInt((byte) 0xFF, (byte) 0xFF));
    }

}
