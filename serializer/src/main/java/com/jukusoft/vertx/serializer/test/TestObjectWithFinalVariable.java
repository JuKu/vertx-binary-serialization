package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SInteger;

@MessageType(type = 0x01, extendedType = 0x02)
@ProtocolVersion(1)
public class TestObjectWithFinalVariable implements SerializableObject {

    @SInteger
    public static final int TEST_INT = 2;

}
