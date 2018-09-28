package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;

@MessageType(type = 0x02)
public class TestObjectWithoutVersion implements SerializableObject {
}
