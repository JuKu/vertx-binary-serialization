package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@MessageType(type = 0x01, extendedType = 0x01)
@ProtocolVersion(1)
public class TestObjectWithBuffer implements SerializableObject {

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

    @SBuffer
    public Buffer buffer = Buffer.buffer();

    @SFloat
    public float floatValue = 1.20f;

    @SDouble
    public double doubleValue = 3.0d;

    @SJsonObject
    public JsonObject json = new JsonObject();

    @SJsonArray
    public JsonArray jsonArray = new JsonArray();

    public TestObjectWithBuffer() {
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
