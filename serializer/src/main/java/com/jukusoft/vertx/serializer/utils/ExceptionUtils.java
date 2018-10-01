package com.jukusoft.vertx.serializer.utils;

public class ExceptionUtils {

    protected ExceptionUtils () {
        //
    }

    public static final void executeWithoutIllegalAccessException (RunnableWithException runnable) {
        try {
            runnable.run();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public static final void executeAndLogException (RunnableWithException runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
