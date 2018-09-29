package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SInteger;
import com.jukusoft.vertx.serializer.annotations.SString;

@MessageType(type = 0x01)
@ProtocolVersion(1)
public class TestObject implements SerializableObject {

    @SInteger
    public int test = 10;

    @SString(maxCharacters = 30)
    public String testStr = "test";

}
