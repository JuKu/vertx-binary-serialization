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
  - [vert.x](http://vertx.io)
  
## HowTo

First create some message objects which contains some datatypes:
```java
@MessageType(type = 0x01)
@ProtocolVersion(1)
public class Message implements SerializableObject {

    @SInteger
    public int test = 0;

}

@MessageType(type = 0x02)
@ProtocolVersion(2)
public class SecondMessage implements SerializableObject {
    
    @SFloat
    public float a = 0f;

    @SString
    public String myString = null;

}
```

You have to add the annotations `MessageType` with the type (1 byte as type, 1 byte as extended type) and `ProtocolVersion` to check, if Serializer on other side can unserialize this object.

Then you can serialize and unserialize this object easely:
```java
//create message object which implements SerializableObject
Message msg = new Message();
msg.test = 20;

SecondMessage msg1 = new SecondMessage();
msg1.a = 0.2f;
msg1.myString = "my-new-string";

//serialize object into byte buffer
Buffer buffer = Serializer.serialize(msg);

//unserialize object from byte buffer
Message obj1 = Serializer.unserialize(buffer);

//get value
System.out.println("test value: " + obj1.test);

//second message

//serialize object into byte buffer
Buffer buffer = Serializer.serialize(msg1);

//unserialize object from byte buffer
SecondMessage obj2 = Serializer.unserialize(buffer);

//get value(s)
System.out.println("float value: " + obj2.a);
System.out.println("string value: " + obj2.myString);
```

**NOTICE**: `public` variables aren't required, they can also be `private` or `protected` instead.
But to avoid getters & setters here, we have accessed them directly in this example.

## Protocol Header

Before adding the payload to buffer, there is adding a header with these fields:
  - maybe: 4x byte (integer) **length of message** (so it can check, if full message was received or we have to wait for other traffic)
  - 1x byte **type**
  - 1x byte **extended byte** (so you can use 65,536 different types, instead of 256 bytes)
  - 2x byte **version** (to check compatibility)
  - after that: **payload data**

## Supported datatypes

All primitive datatypes in Java are supported:

  - byte (`@SByte`)
  - short (`@SShort`)
  - int (`@SInteger`)
  - long (`@SLong`)
  - float (`@SFloat`)
  - double (`@SDouble`)
  - boolean (`@SBoolean`)
  - char (`@SChar`)
  - Vertx. Buffer (`@SBuffer`)
  - byte array (max 4.294.967.296 bytes in an array, `@SBytes`)
  
**Complex datatypes** (objects) are **not** supported!

## Run Sonarcloud

```bash
clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar -Dsonar.host.url=https://sonarcloud.io -Dsonar.organization=jukusoft -Dsonar.login=<Sonar-Token>
```