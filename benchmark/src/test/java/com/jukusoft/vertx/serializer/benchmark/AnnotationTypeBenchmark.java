package com.jukusoft.vertx.serializer.benchmark;

import com.jukusoft.vertx.serializer.annotations.SInteger;
import com.jukusoft.vertx.serializer.annotations.SString;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.Random;

public class AnnotationTypeBenchmark {

    @Test
    public void testAnnotationType () {
        Annotation[] testData = new Annotation[1000000];
        String[] test = new String[10000000];

        Random random = new Random();

        for (int i = 0; i < testData.length; i++) {
            if (random.nextInt() % 2 == 0) {
                testData[i] = new SInteger() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return SInteger.class;
                    }

                    @Override
                    public int minValue() {
                        return 0;
                    }

                    @Override
                    public int maxValue() {
                        return 0;
                    }
                };
            } else {
                testData[i] = new SString() {

                    @Override
                    public int maxCharacters() {
                        return 0;
                    }

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return SString.class;
                    }
                };
            }
        }

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < testData.length; i++) {
            Annotation annotation = testData[i];

            Class<? extends Annotation> clazz = annotation.annotationType();

            if (clazz == SInteger.class) {
                test[i] = "SInteger";
            } else if (clazz == SString.class) {
                test[i] = "SString";
            }
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.err.println("[Benchmark] comparision with clazz == SInteger.class takes " + timeDiff + "ms.");

        test = new String[10000000];

        startTime = System.currentTimeMillis();

        for (int i = 0; i < testData.length; i++) {
            Annotation annotation = testData[i];

            if (annotation instanceof SInteger) {
                test[i] = "SInteger";
            } else if (annotation instanceof SString) {
                test[i] = "SString";
            }
        }

        endTime = System.currentTimeMillis();
        timeDiff = endTime - startTime;
        System.err.println("[Benchmark] comparision with clazz instanceof SString takes " + timeDiff + "ms.");
    }

}
