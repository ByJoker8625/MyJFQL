package de.jokergames.jfql.util;

/**
 * @author Janick
 */

public class Cryptor {

    private final int key;

    public Cryptor(int key) {
        this.key = key;
    }

    public String encrypt(String decrypt) {
        char[] decryptCharacters = decrypt.toCharArray();
        char[] encryptCharacters = new char[decryptCharacters.length];

        for (int j = 0; j < decryptCharacters.length; j++) {
            char character = decryptCharacters[j] -= key;
            encryptCharacters[j] = character;
        }

        return new String(encryptCharacters);
    }

    public String decrypt(String encrypt) {
        char[] encryptCharacters = encrypt.toCharArray();
        char[] decryptCharacters = new char[encryptCharacters.length];

        for (int j = 0; j < encryptCharacters.length; j++) {
            char character = encryptCharacters[j] += key;
            decryptCharacters[j] = character;
        }

        return new String(decryptCharacters);
    }

}
