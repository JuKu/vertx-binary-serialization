package com.jukusoft.vertx.serializer;

import com.jukusoft.vertx.serializer.annotations.*;
import com.jukusoft.vertx.serializer.exceptions.NoMessageTypeException;
import com.jukusoft.vertx.serializer.exceptions.NoProtocolVersionException;
import com.jukusoft.vertx.serializer.exceptions.SerializerException;
import com.jukusoft.vertx.serializer.exceptions.UnsupportedProtocolVersionException;
import com.jukusoft.vertx.serializer.utils.ByteUtils;
import com.jukusoft.vertx.serializer.utils.ExceptionUtils;
import io.vertx.core.buffer.Buffer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Serializer {

    protected Serializer () {
        //
    }

    public static final Buffer serialize (SerializableObject obj) {
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

        final int pos = _pos;

        ExceptionUtils.executeWithoutIllegalAccessException(() -> serializeFields(buf, obj, pos));

        return buf;
    }

    protected static final void serializeFields (Buffer buf, SerializableObject obj, int _pos) throws IllegalAccessException {
        //iterate through all fields in class
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

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
                } else if (clazz == SBoolean.class) {
                    boolean bool = field.getBoolean(obj);

                    //add to protocol
                    buf.setByte(_pos, (byte) (bool ? 0x01 : 0x02));
                    _pos += 1;
                } else if (clazz == SChar.class) {
                    char character = field.getChar(obj);

                    //add to protocol
                    buf.setShort(_pos, (short) character);
                    _pos += 2;
                } else if (clazz == SByte.class) {
                    byte b = field.getByte(obj);

                    //add to protocol
                    buf.setByte(_pos, b);
                    _pos += 1;
                } else if (clazz == SBytes.class) {
                    byte[] bytes = (byte[]) field.get(obj);

                    if (bytes == null) {
                        throw new NullPointerException("byte array cannot be null in field '" + field.getName() + "' in class '" + clazz.getCanonicalName() + "'!");
                    }

                        /*if (bytes.length > 4294967296l) {
                            throw new SerializerException("max 4294967296 bytes are allowed in array in field '" + field.getName() + "' class '" + clazz.getCanonicalName() + "'!");
                        }*/

                    //add to protocol
                    buf.setUnsignedInt(_pos, bytes.length);
                    _pos += 4;

                    buf.setBytes(_pos, bytes);
                    _pos += bytes.length;
                } else if (clazz == SShort.class) {
                    short s = field.getShort(obj);

                    //add to protocol
                    buf.setShort(_pos, s);
                    _pos += 2;
                } else if (clazz == SLong.class) {
                    long l = field.getLong(obj);

                    //add to protocol
                    buf.setLong(_pos, l);
                    _pos += 8;
                } else if (clazz == SFloat.class) {
                    float floatValue = field.getFloat(obj);

                    //add to protocol
                    buf.setFloat(_pos, floatValue);
                    _pos += 4;
                } else if (clazz == SDouble.class) {
                    double d = field.getDouble(obj);

                    //add to protocol
                    buf.setDouble(_pos, d);
                    _pos += 8;
                } else if (clazz == SBuffer.class) {
                    Buffer content = (Buffer) field.get(obj);

                    if (content == null) {
                        throw new NullPointerException("buffer in field '" + field.getName() + "' in class '" + clazz.getCanonicalName() + "' cannot be null!");
                    }

                    //add length of buffer
                    buf.setInt(_pos, content.length());
                    _pos += 4;

                    //add string
                    buf.setBuffer(_pos, content);
                    _pos += content.length();
                }
            }
        }
    }

    public static <T extends SerializableObject> T unserialize (Buffer msg, Class<T> cls, int _pos) {
        //first, create new instance of this class
        T ins = null;

        try {
            ins = cls.newInstance();
        } catch (InstantiationException e) {
            throw new SerializerException("Cannot create new instance of class " + cls.getCanonicalName() + "! Maybe constructor isn't public or class is abstract?");
        } catch (IllegalAccessException e) {
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
                field.setAccessible(true);

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
                        _pos += length * 4;

                        field.set(ins, str);
                    } else if (clazz == SBoolean.class) {
                        boolean bool = msg.getByte(_pos) == (byte) 0x01;
                        _pos += 1;

                        field.set(ins, bool);
                    } else if (clazz == SChar.class) {
                        char character = (char) msg.getShort(_pos);
                        _pos += 2;

                        field.set(ins, character);
                    } else if (clazz == SByte.class) {
                        byte b = msg.getByte(_pos);
                        _pos += 1;

                        //add to protocol
                        field.set(ins, b);
                    } else if (clazz == SBytes.class) {
                        int length = (int) msg.getUnsignedInt(_pos);
                        _pos += 4;

                        byte[] bytes = msg.getBytes(_pos, _pos + length);
                        _pos += length;

                        field.set(ins, bytes);
                    } else if (clazz == SShort.class) {
                        short s = msg.getShort(_pos);
                        _pos += 2;

                        field.set(ins, s);
                    } else if (clazz == SLong.class) {
                        long l = msg.getLong(_pos);
                        _pos += 8;

                        field.set(ins, l);
                    } else if (clazz == SFloat.class) {
                        float floatValue = msg.getFloat(_pos);
                        _pos += 4;

                        field.set(ins, floatValue);
                    } else if (clazz == SDouble.class) {
                        double d = msg.getDouble(_pos);
                        _pos += 8;

                        field.set(ins, d);
                    } else if (clazz == SBuffer.class) {
                        //get length of buffer
                        int length = msg.getInt(_pos);
                        _pos += 4;

                        Buffer content = msg.getBuffer(_pos, _pos + length);
                        _pos += length;

                        field.set(ins, content);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new SerializerException("Cannot set value in class '" + cls.getCanonicalName() + "'! Maybe a field is static or final?");
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
