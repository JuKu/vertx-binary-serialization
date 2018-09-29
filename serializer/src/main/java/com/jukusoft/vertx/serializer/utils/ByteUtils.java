package com.jukusoft.vertx.serializer.utils;

public class ByteUtils {

    protected ByteUtils () {
        //
    }

    public static int twoBytesToInt (byte type, byte extendedType) {
        return ((type & 0xff) << 8) | (extendedType & 0xff);
    }

}
