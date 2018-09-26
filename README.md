# vertx-binary-serialization

A simple binary serialization method for vertx which uses annotations &amp; reflection like spring jpa for databases.

## Requirements

  - Java 8+
  - [vertx](http://vertx.io)
  
## HowTo

First create a message object which contains some types (in this case only one integer):
```java
@MessageType(type = 0x01)
@ProtocolVersion(1)
public class TestObject extends SerializableObject {

    @SInteger
    public int test = 10;

}
```

You have to add the annotations `MessageType` with the type (1 byte as type) and `ProtocolVersion` to check, if Serializer on other side can unserialize this object.

Then you can serialize and unserialize this object easely:
```java
//create message object which implements SerializableObject
TestObject obj = new TestObject();

//serialize object into byte buffer
Buffer buffer = Serializer.serialize(obj);

//unserialize object from byte buffer
TestObject obj1 = Serializer.unserialize(buffer, TestObject.class);
```