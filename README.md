# vertx-binary-serialization

A simple binary serialization method for vertx which uses annotations &amp; reflection like spring jpa for databases.

[![Build Status](https://travis-ci.org/JuKu/vertx-binary-serialization.svg?branch=master)](https://travis-ci.org/JuKu/vertx-binary-serialization)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Avertx-binary-serializer-parent&metric=ncloc)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Avertx-binary-serializer-parent) 
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Avertx-binary-serializer-parent&metric=alert_status)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Avertx-binary-serializer-parent) 
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Avertx-binary-serializer-parent&metric=coverage)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Avertx-binary-serializer-parent) 
[![Technical Debt Rating](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Avertx-binary-serializer-parent&metric=sqale_index)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Avertx-binary-serializer-parent) 
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Avertx-binary-serializer-parent&metric=code_smells)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Avertx-binary-serializer-parent) 
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Avertx-binary-serializer-parent&metric=bugs)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Avertx-binary-serializer-parent) 
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Avertx-binary-serializer-parent&metric=vulnerabilities)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Avertx-binary-serializer-parent) 
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Avertx-binary-serializer-parent&metric=security_rating)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Avertx-binary-serializer-parent) 

[![Sonarcloud](https://sonarcloud.io/api/project_badges/quality_gate?project=com.jukusoft%3Avertx-binary-serializer-parent)](https://sonarcloud.io/dashboard?id=com.jukusoft%3Avertx-binary-serializer-parent)

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

## Supported datatypes

All primitive datatypes in Java are supported:

  - byte
  - short
  - int
  - long
  - float
  - double
  - boolean
  - char
  - byte array (max 4.294.967.296 bytes in an array)
  
**Complex datatypes** (objects) are **not** supported yet!