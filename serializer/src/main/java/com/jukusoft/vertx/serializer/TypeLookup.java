package com.jukusoft.vertx.serializer;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.utils.ByteUtils;
import com.jukusoft.vertx.serializer.utils.ExceptionUtils;

public class TypeLookup {

    protected static IntObjectMap<Class<? extends SerializableObject>> map = new IntObjectHashMap<>();

    protected TypeLookup () {
        //
    }

    public static void register (byte type, byte extendedType, Class<? extends SerializableObject> cls) {
        int key = ByteUtils.twoBytesToInt(type, extendedType);

        //OPTIMIZATION: warm up jvm --> serialize 3 times, so JVM C2 compiler will assembles Java bytecode to assembler directly
        ExceptionUtils.executeAndLogException(() -> {
            Serializer.serialize(cls.newInstance());
            Serializer.serialize(cls.newInstance());
            Serializer.serialize(cls.newInstance());
        });

        map.put(key, cls);
    }

    public static void register (Class<? extends SerializableObject> cls) {
        MessageType type = cls.getAnnotation(MessageType.class);

        if (type == null) {
            throw new IllegalArgumentException("class '" + cls.getCanonicalName() + "' doesn't contains annotation @MessageType !");
        }

        register(type.type(), type.extendedType(), cls);
    }

    public static void unregister (byte type, byte extendedType) {
        int key = ByteUtils.twoBytesToInt(type, extendedType);
        map.remove(key);
    }

    public static void unregister (Class<? extends SerializableObject> cls) {
        MessageType type = cls.getAnnotation(MessageType.class);

        if (type == null) {
            throw new IllegalArgumentException("class '" + cls.getCanonicalName() + "' doesn't contains annotation @MessageType !");
        }

        unregister(type.type(), type.extendedType());
    }

    public static Class<? extends SerializableObject> find (byte type, byte extendedType) {
        int key = ByteUtils.twoBytesToInt(type, extendedType);
        return map.get(key);
    }

    public static void removeAll () {
        map.clear();
    }

}
