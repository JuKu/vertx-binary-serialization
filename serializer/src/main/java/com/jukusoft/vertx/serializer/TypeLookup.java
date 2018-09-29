package com.jukusoft.vertx.serializer;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.jukusoft.vertx.serializer.utils.ByteUtils;

public class TypeLookup {

    protected static IntObjectMap<Class<? extends SerializableObject>> map = new IntObjectHashMap<>();

    protected TypeLookup () {
        //
    }

    public static void register (byte type, byte extendedType, Class<? extends SerializableObject> cls) {
        int key = ByteUtils.twoBytesToInt(type, extendedType);
        map.put(key, cls);
    }

    public static void unregister (byte type, byte extendedType) {
        int key = ByteUtils.twoBytesToInt(type, extendedType);
        map.remove(key);
    }

    public static Class<? extends SerializableObject> find (byte type, byte extendedType) {
        int key = ByteUtils.twoBytesToInt(type, extendedType);
        return map.get(key);
    }

}
