package com.jukusoft.vertx.serializer;

import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SInteger;
import com.jukusoft.vertx.serializer.annotations.SString;
import com.jukusoft.vertx.serializer.exceptions.NoMessageTypeException;
import com.jukusoft.vertx.serializer.exceptions.NoProtocolVersionException;
import com.jukusoft.vertx.serializer.exceptions.SerializerException;
import com.jukusoft.vertx.serializer.exceptions.UnsupportedProtocolVersionException;
import com.jukusoft.vertx.serializer.utils.ByteUtils;
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

        MessageType msgType = obj.getClass().getAnnotation(MessageType.class);

        if (msgType.type() == 0x00) {
            throw new IllegalStateException("message type cannot 0x00, please correct annotation @MessageType in class '" + obj.getClass().getCanonicalName()+ "'!");
        }

        if (msgType.extendedType() == 0x00) {
            throw new IllegalStateException("message extended type cannot 0x00, please correct annotation @MessageType in class '" + obj.getClass().getCanonicalName()+ "'!");
        }

        //add message type
        buf.setByte(_pos, msgType.type());
        _pos += 1;

        //add message extended type
        buf.setByte(_pos, msgType.extendedType());
        _pos += 1;

        //add protocol version
        ProtocolVersion version = obj.getClass().getAnnotation(ProtocolVersion.class);
        buf.setShort(_pos, version.value());
        _pos += 2;

        try {
            //iterate through all fields in class
            for (Field field : obj.getClass().getDeclaredFields()) {
                //iterate through all annotations for this field
                for (Annotation annotation : field.getAnnotations()) {
                    Class<? extends Annotation> clazz = annotation.annotationType();

                    if (clazz == SInteger.class) {
                        //get integer value of field
                        int value = field.getInt(obj);

                        //check range

                        //add to protocol
                        buf.setInt(_pos, value);
                        _pos += 4;
                    } else if (clazz == SString.class) {
                        String value = (String) field.get(obj);

                        //check max characters

                        //add length of string
                        buf.setInt(_pos, value.length());
                        _pos += 4;

                        //add string
                        buf.setString(_pos, value);
                        _pos += value.length() * 4;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return buf;
    }

    public static <T extends SerializableObject> T unserialize (Buffer msg, Class<T> cls, int _pos) {
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

        if (cls.getAnnotation(ProtocolVersion.class) == null) {
            throw new NoProtocolVersionException("No protocol version annotation was found in class '" + cls.getCanonicalName() + "'!");
        }

        //read type
        //byte type = msg.getByte(_pos);
        //_pos += 1;

        //read extended type
        //byte extendedType = msg.getByte(_pos);
        //_pos += 1;

        if (_pos < 2) {
            throw new IllegalArgumentException("_pos cannot be < 2, because type and extended type are also in protocol.");
        }

        //read protocol version
        short version = msg.getShort(_pos);
        _pos += 2;

        //check protocol version
        ProtocolVersion versionObj = cls.getAnnotation(ProtocolVersion.class);

        if (version != versionObj.value()) {
            throw new UnsupportedProtocolVersionException("given protocol version '" + version + "' isn't compatible with local protocol version '" + versionObj.value() + "'!");
        }

        try {
            //iterate through all fields in class
            for (Field field : cls.getDeclaredFields()) {
                //iterate through all annotations for this field
                for (Annotation annotation : field.getAnnotations()) {
                    Class<? extends Annotation> clazz = annotation.annotationType();

                    //set field accessible, so we can change value
                    field.setAccessible(true);

                    if (clazz == SInteger.class) {
                        //read int
                        int value = msg.getInt(_pos);
                        _pos += 4;

                        field.set(ins, value);
                    } else if (clazz == SString.class) {
                        //read length of string
                        int length = msg.getInt(_pos);
                        _pos += 4;

                        //read string
                        String str = msg.getString(_pos, _pos + length);

                        field.set(ins, str);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return ins;
    }

    public static <T extends SerializableObject> T unserialize (Buffer msg, Class<T> cls) {
        return unserialize(msg, cls, 2);
    }
    public static <T extends SerializableObject> T unserialize (Buffer msg) {
        int _pos = 0;

        //get type
        byte type = msg.getByte(_pos);
        _pos += 1;

        //get extended type
        byte extendedType = msg.getByte(_pos);
        _pos += 1;

        //get class
        Class<T> cls = (Class<T>) TypeLookup.find(type, extendedType);

        if (cls == null) {
            throw new IllegalStateException("message type " + ByteUtils.byteToHex(type) + " with extended type " + ByteUtils.byteToHex(extendedType) + " doesn't have a registered class, please register with TypeLookup.register() first!");
        }

        return cls.cast(unserialize(msg, cls, _pos));
    }

}
