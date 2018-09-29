package com.jukusoft.vertx.serializer.utils;

public class ByteUtils {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    protected ByteUtils () {
        //
    }

    public static int twoBytesToInt (byte type, byte extendedType) {
        return ((type & 0xff) << 8) | (extendedType & 0xff);
    }

    /**
     * converts byte array to hex string
     *
     * @param bytes array of bytes to convert
     *
     * @author https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
     *
     * @return hex string
     */
    public static String bytesToHex (byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    public static String byteToHex (byte type) {
        byte[] bytes = new byte[] { type };

        return bytesToHex(bytes);
    }

}
