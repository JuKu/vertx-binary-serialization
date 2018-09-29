package com.jukusoft.vertx.serializer.test;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.*;

@MessageType(type = 0x02)
@ProtocolVersion(1)
public class TestObject1 implements SerializableObject {

    @SInteger
    public int test = 1;

    @SString(maxCharacters = 30)
    public String testStr = "test_";

    @DummyAnnotation
    public String testStr1 = "test";

    public TestObject1 () {
        //
    }

}
