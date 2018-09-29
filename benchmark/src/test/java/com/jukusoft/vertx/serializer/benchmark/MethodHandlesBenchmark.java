package com.jukusoft.vertx.serializer.benchmark;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.test.TestObject;
import com.jukusoft.vertx.serializer.test.TestObject1;
import com.jukusoft.vertx.serializer.utils.ByteUtils;
import org.junit.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Random;

public class MethodHandlesBenchmark {

    @Test
    public void testReflectionWay () throws IllegalAccessException, InstantiationException {
        Class<? extends SerializableObject>[] classes = new Class[1000000];

        Random random = new Random();

        for (int i = 0; i < classes.length; i++) {
            classes[i] = random.nextInt() % 2 == 0 ? TestObject.class : TestObject1.class;
        }

        SerializableObject[] objs = new SerializableObject[classes.length];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < classes.length; i++) {
            objs[i] = classes[i].newInstance();
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] instance creation with reflection takes " + timeDiff + "ms");
    }

    @Test
    public void testMethodHandleWay () throws Throwable {
        Class<? extends SerializableObject>[] classes = new Class[1000000];

        ObjectObjectMap<Class<? extends SerializableObject>,MethodHandle> handles = new ObjectObjectHashMap<>();
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        Random random = new Random();

        for (int i = 0; i < classes.length; i++) {
            classes[i] = random.nextInt() % 2 == 0 ? TestObject.class : TestObject1.class;

            //2 parameters: return type and input types
            MethodType mt = MethodType.methodType(void.class);//classes[i]

            //find method handle
            MethodHandle constructorMH = lookup.findConstructor(classes[i], mt);

            //get @MessageType annotation
            MessageType type = classes[i].getAnnotation(MessageType.class);

            if (type == null) {
                throw new IllegalStateException("No annotation @MessageType is set for class '" + classes[i].getCanonicalName() + "'!");
            }

            handles.put(classes[i], constructorMH);
        }

        SerializableObject[] objs = new SerializableObject[classes.length];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < classes.length; i++) {
            //get method handle
            MethodHandle constructorMH = handles.get(classes[i]);

            objs[i] = (SerializableObject) constructorMH.invoke();
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] instance creation with method handles takes " + timeDiff + "ms");
    }

}
