package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.*;

@MessageType(type = 0x01, extendedType = 0x01)
@ProtocolVersion(1)
public class TestObject implements SerializableObject {

    @SInteger
    public int test = 10;

    @SString(maxCharacters = 30)
    public String testStr = "test";

    @SBoolean
    protected boolean testBool = false;

    @SChar
    private char testChar = 'z';

    @SByte
    public byte b = 0x10;

    @SBytes
    public byte[] bytes = new byte[0];

    @SShort
    public short shortValue = 100;

    @SLong
    public long longValue = 40;

    @SFloat
    public float floatValue = 1.20f;

    @SDouble
    public double doubleValue = 3.0d;

    public TestObject () {
        //
    }

    public boolean isTestBool() {
        return testBool;
    }

    public void setTestBool(boolean testBool) {
        this.testBool = testBool;
    }

    public char getTestChar() {
        return testChar;
    }

    public void setTestChar(char testChar) {
        this.testChar = testChar;
    }

}
