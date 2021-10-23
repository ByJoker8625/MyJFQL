package de.byjoker.myjfql.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Encryptor implements Encryptor {

    @Override
    public String encrypt(String s) {
        return new String(Base64.getEncoder().encode(s.getBytes(StandardCharsets.UTF_8)));
    }
}
