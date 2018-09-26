package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SInteger;

@MessageType(type = 0x01)
@ProtocolVersion(1)
public class TestObject extends SerializableObject {

    @SInteger
    public int test = 10;

}
