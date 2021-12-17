package de.byjoker.myjfql.server.encryptor;

public class NoneEncryptor implements Encryptor {

    @Override
    public String encrypt(String s) {
        return s;
    }

    @Override
    public String name() {
        return "NONE";
    }

}
