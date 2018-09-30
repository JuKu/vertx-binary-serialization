package com.jukusoft.vertx.serializer.utils;

import org.junit.Test;

public class ExceptionUtilsTest {

    @Test
    public void testConstructor () {
        new ExceptionUtils();
    }

    @Test
    public void testFireIllegalAccessException () {
        ExceptionUtils.executeWithoutIllegalAccessException(() -> {
            throw new IllegalAccessException("test");
        });
    }

    @Test (expected = IllegalArgumentException.class)
    public void testFireOtherException () {
        ExceptionUtils.executeWithoutIllegalAccessException(() -> {
            throw new IllegalArgumentException("test");
        });
    }

    @Test (expected = RuntimeException.class)
    public void testFireRuntimeException () {
        ExceptionUtils.executeWithoutIllegalAccessException(() -> {
            throw new RuntimeException("test");
        });
    }

}
