package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SBuffer;
import io.vertx.core.buffer.Buffer;

@MessageType(type = 0x01, extendedType = 0x01)
@ProtocolVersion(1)
public class TestObjectWithNullBuffer implements SerializableObject {

    @SBuffer
    protected Buffer buffer = null;

}
