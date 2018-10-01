package com.jukusoft.vertx.serializer.utils;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testExecuteAndLogException () {
        AtomicBoolean b = new AtomicBoolean(false);

        ExceptionUtils.executeAndLogException(() -> b.set(true));

        //check, if handler was executed
        assertEquals(true, b.get());
    }

    @Test
    public void testExecuteAndLogExceptionWithException () {
        AtomicBoolean b = new AtomicBoolean(false);

        ExceptionUtils.executeAndLogException(() -> {
            b.set(true);

            throw new RuntimeException("test");
        });

        //check, if handler was executed
        assertEquals(true, b.get());
    }

}
