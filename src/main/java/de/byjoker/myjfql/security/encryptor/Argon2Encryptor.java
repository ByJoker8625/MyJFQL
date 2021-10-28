package de.byjoker.myjfql.security.encryptor;

import de.mkammerer.argon2.Argon2Advanced;
import de.mkammerer.argon2.Argon2Factory;

import java.nio.charset.StandardCharsets;

public class Argon2Encryptor implements Encryptor {

    final Argon2Advanced argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id);

    @Override
    public String encrypt(String s) {
        return new String(argon2.rawHash(10, 1576, 4, "argon2".toCharArray(), s.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public String name() {
        return "ARGON2";
    }

}
