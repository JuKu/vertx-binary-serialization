package com.jukusoft.vertx.serializer;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

public class TypeLookup {

    protected static IntObjectMap<Class<? extends SerializableObject>> map = new IntObjectHashMap<>();

    protected TypeLookup () {
        //
    }

    public static void register (byte type, byte extendedType, Class<? extends SerializableObject> cls) {
        int key = bytesToInt(type, extendedType);
        map.put(key, cls);
    }

    public static void unregister (byte type, byte extendedType) {
        int key = bytesToInt(type, extendedType);
        map.remove(key);
    }

    public static Class<? extends SerializableObject> find (byte type, byte extendedType) {
        int key = bytesToInt(type, extendedType);
        return map.get(key);
    }

    protected static int bytesToInt (byte type, byte extendedType) {
        int val = ((type & 0xff) << 8) | (extendedType & 0xff);
        return val;
    }

}
