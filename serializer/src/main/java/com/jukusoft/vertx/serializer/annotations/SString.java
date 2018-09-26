package com.jukusoft.vertx.serializer.annotations;

import java.nio.charset.StandardCharsets;

public @interface SString {

    public int maxCharacters ();

    //TODO: add support for encoding

}
