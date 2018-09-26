package com.jukusoft.vertx.serializer.benchmark;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.ShortObjectHashMap;
import com.carrotsearch.hppc.ShortObjectMap;
import javolution.util.FastMap;
import org.junit.Test;

import java.util.Random;

public class CollectionBenchmark {

    @Test
    public void testHppcIntObjectMap () {
        IntObjectMap<Object> map = new IntObjectHashMap<>();

        int nOfObjects = 1000000;

        long startTime = System.currentTimeMillis();

        //add 1.000.000 entries
        for (int i = 0; i < nOfObjects; i++) {
            map.put(i, new Object());
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] creation and putting 1.000.000 objects to hppc int map takes " + timeDiff + "ms.");

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
        System.out.println("[Benchmark] access 1.000.000 random entries from int map takes " + timeDiff + "ms.");
    }

    @Test
    public void testHppcShortObjectMap () {
        ShortObjectMap<Object> map = new ShortObjectHashMap<>();

        int nOfObjects = Short.MAX_VALUE;

        long startTime = System.currentTimeMillis();

        //add 1.000.000 entries
        for (short i = 0; i < nOfObjects; i++) {
            map.put(i, new Object());
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] creation and putting " + nOfObjects + " objects to hppc short map takes " + timeDiff + "ms.");

        Random random = new Random();

        int min = 0;
        int max = nOfObjects - 1;

        startTime = System.currentTimeMillis();

        for (int i = 0; i < nOfObjects; i++) {
            //get random index
           short index = (short) (random.nextInt((max - min) + 1) + min);

            //get object
            Object obj = map.get(index);
        }

        endTime = System.currentTimeMillis();
        timeDiff = endTime - startTime;
        System.out.println("[Benchmark] access " + nOfObjects + " random entries from short map takes " + timeDiff + "ms.");
    }

    @Test
    public void tesJavolutionMap () {
        FastMap<Integer,Object> map = new FastMap<>();

        int nOfObjects = 1000000;

        long startTime = System.currentTimeMillis();

        //add 1.000.000 entries
        for (int i = 0; i < nOfObjects; i++) {
            map.put(i, new Object());
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("[Benchmark] creation and putting 1.000.000 objects to FastMap takes " + timeDiff + "ms.");

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
        System.out.println("[Benchmark] access 1.000.000 random entries from FastMap takes " + timeDiff + "ms.");
    }

}
