package de.byjoker.myjfql.util;

public class NoneEncryptor implements Encryptor {

    @Override
    public String encrypt(String s) {
        return s;
    }

}
