package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SInteger;
import com.jukusoft.vertx.serializer.annotations.SString;

@MessageType(type = 0x01, extendedType = 0x01)
@ProtocolVersion(2)
public class TestObjectV1 implements SerializableObject {

    @SInteger
    public int test = 10;

    @SString(maxCharacters = 30)
    public String testStr = "test";

    public TestObjectV1() {
        //
    }

}
