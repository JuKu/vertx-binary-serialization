package com.jukusoft.vertx.serializer;

import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SInteger;
import com.jukusoft.vertx.serializer.annotations.SString;
import com.jukusoft.vertx.serializer.exceptions.NoMessageTypeException;
import com.jukusoft.vertx.serializer.exceptions.NoProtocolVersionException;
import com.jukusoft.vertx.serializer.exceptions.SerializerException;
import io.vertx.core.buffer.Buffer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Serializer {

    protected Serializer () {
        //
    }

    public static Buffer serialize (SerializableObject obj) {
        Buffer buf = Buffer.buffer();

        if (obj.getClass().getAnnotation(MessageType.class) == null) {
            throw new NoMessageTypeException("No message type annotation was found in class '" + obj.getClass().getCanonicalName() + "'!");
        }

        if (obj.getClass().getAnnotation(ProtocolVersion.class) == null) {
            throw new NoProtocolVersionException("No protocol version annotation was found in class '" + obj.getClass().getCanonicalName() + "'!");
        }

        int _pos = 0;

        //add message type
        MessageType msgType = obj.getClass().getAnnotation(MessageType.class);
        buf.setInt(_pos, msgType.type());
        _pos += 4;

        //add message extended type
        buf.setInt(_pos, msgType.extendedByte());
        _pos += 4;

        try {
            //iterate through all fields in class
            for (Field field : obj.getClass().getDeclaredFields()) {
                //iterate through all annotations for this field
                for (Annotation annotation : field.getAnnotations()) {
                    Class<? extends Annotation> clazz = annotation.annotationType();

                    if (annotation instanceof SInteger) {
                        //get integer value of field
                        int value = field.getInt(obj);

                        //check range

                        //add to protocol
                        buf.setInt(_pos, value);
                    } else if (annotation instanceof SString) {
                        //TODO: add code here
                    }

                    //TODO: add code here
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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
