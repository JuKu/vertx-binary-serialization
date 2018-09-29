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
