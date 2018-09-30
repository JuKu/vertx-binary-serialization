package com.jukusoft.vertx.connection.clientserver;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.test.TestObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class HandlerManagerImplTest {

    @Test
    public void testConstructor () {
        new HandlerManagerImpl();
    }

    @Test (expected = IllegalStateException.class)
    public void testFindUnregisteredHandler () {
        HandlerManager manager = new HandlerManagerImpl();
        manager.findHandler(String.class);
    }

    @Test
    public void testRegister () {
        HandlerManager manager = new HandlerManagerImpl();

        //register handler
        manager.register(TestObject.class, (msg, conn) -> {
            //do something
        });

        //check, if handler is registered
        MessageHandler handler = manager.findHandler(TestObject.class);
        assertNotNull(handler);
    }

    @Test (expected = IllegalStateException.class)
    public void testRegisterWithoutMessageType () {
        HandlerManager manager = new HandlerManagerImpl();

        //register handler
        manager.register(String.class, (msg, conn) -> {
            //do something
        });
    }

    @Test
    public void testGetMessageTypeAnnotation () {
        HandlerManager manager = new HandlerManagerImpl();

        //register handler
        ((HandlerManagerImpl) manager).getMessageTypeAnnotation(TestObject.class);
    }

    @Test (expected = IllegalStateException.class)
    public void testGetNullMessageTypeAnnotation () {
        HandlerManager manager = new HandlerManagerImpl();

        //register handler
        ((HandlerManagerImpl) manager).getMessageTypeAnnotation(String.class);
    }

    @Test
    public void testRegisterAndUnregister () {
        HandlerManager manager = new HandlerManagerImpl();

        //register handler
        manager.register(TestObject.class, (msg, conn) -> {
            //do something
        });

        //check, if handler is registered
        MessageHandler handler = manager.findHandler(TestObject.class);
        assertNotNull(handler);

        //unregister handler
        manager.unregister(TestObject.class);
    }

    @Test
    public void testRegisterAndUnregister1 () {
        HandlerManager manager = new HandlerManagerImpl();

        //register handler
        manager.register(TestObject.class, (msg, conn) -> {
            //do something
        });

        //check, if handler is registered
        MessageHandler handler = manager.findHandler(TestObject.class);
        assertNotNull(handler);

        //unregister handler
        manager.unregister(TestObject.class);
        assertNull(manager.findHandler(TestObject.class));
    }

}
