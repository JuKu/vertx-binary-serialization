package com.jukusoft.vertx.serializer.utils;

public class ExceptionUtils {

    protected ExceptionUtils () {
        //
    }

    public static final void executeWithoutIllegalAccessException (RunnableWithException runnable) {
        try {
            runnable.run();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            throw e;
        }
    }

}
