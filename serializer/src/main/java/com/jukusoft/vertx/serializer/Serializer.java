package com.jukusoft.vertx.serializer;

import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.exceptions.NoProtocolVersionException;
import com.jukusoft.vertx.serializer.exceptions.SerializerException;
import io.vertx.core.buffer.Buffer;

public class Serializer {

    protected Serializer () {
        //
    }

    public static Buffer serialize (SerializableObject obj) {
        Buffer buf = Buffer.buffer();

        if (obj.getClass().getAnnotation(ProtocolVersion.class) == null) {
            throw new NoProtocolVersionException("No protocol version annotation was found in class '" + obj.getClass().getCanonicalName() + "'!");
        }

        //TODO: add code here

        return buf;
    }

    public static <T extends SerializableObject> T unserialize (Buffer msg, Class<T> cls) {
        //first, create new instance of this class
        T ins = null;

        try {
            ins = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new SerializerException("Cannot create new instance of class " + cls.getCanonicalName() + "! Maybe constructor isn't public?");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new SerializerException("Cannot access class " + cls.getCanonicalName() + "! Maybe constructor isn't public?");
        }

        return ins;
    }

}
