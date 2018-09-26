package com.jukusoft.vertx.serializer.benchmark;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import org.junit.Test;

import java.util.Random;

public class CollectionBenchmark {

    @Test
    public void testHppc () {
        IntObjectMap<Object> map = new IntObjectHashMap<>();

        int nOfObjects = 1000000;

        long startTime = System.currentTimeMillis();

        //add 1.000.000 entries
        for (int i = 0; i < nOfObjects; i++) {
            map.put(i, new Object());
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] creation and putting 1.000.000 objects to hppc map takes " + timeDiff + "ms.");

        Random random = new Random();

        int min = 0;
        int max = nOfObjects - 1;

        startTime = System.currentTimeMillis();

        for (int i = 0; i < nOfObjects; i++) {
            //get random index
            int index = random.nextInt((max - min) + 1) + min;

            //get object
            Object obj = map.get(index);
        }

        endTime = System.currentTimeMillis();
        timeDiff = endTime - startTime;
        System.out.println("[Benchmark] access 1.000.000 random entries takes " + timeDiff + "ms.");
    }

}
