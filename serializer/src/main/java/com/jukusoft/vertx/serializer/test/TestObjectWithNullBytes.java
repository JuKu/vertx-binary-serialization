package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SBytes;

@MessageType(type = 0x01, extendedType = 0x02)
@ProtocolVersion(1)
public class TestObjectWithNullBytes implements SerializableObject {

    @SBytes
    public byte[] bytes = null;

}
