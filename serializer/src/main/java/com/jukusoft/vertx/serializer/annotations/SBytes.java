package com.jukusoft.vertx.serializer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* annotation used to serialize byte arrays
 * for example:
 *
 * <pre>{@code
 * @SBytes
 * public byte[] bytes = new byte[0];
 * }</pre>
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SBytes {
}
