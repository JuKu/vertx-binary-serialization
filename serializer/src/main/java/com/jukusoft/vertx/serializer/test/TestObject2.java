package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;

@MessageType(type = 0x01, extendedType = 0x02)
@ProtocolVersion(1)
public class TestObject2 implements SerializableObject {
}
