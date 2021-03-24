package org.jokergames.myjfql.encryption;

import java.util.stream.IntStream;

public class DDPEncryption extends Encryption {

    public DDPEncryption() {
        super("ddp", new Protocol() {
            @Override
            public String encrypt(String output, String key) {
                try {
                    int j = Integer.parseInt(key);
                    char[] decryptedCharacters = output.toCharArray();
                    char[] encryptedCharacters = new char[decryptedCharacters.length];

                    IntStream.range(0, decryptedCharacters.length).forEach(i -> encryptedCharacters[i] = (char) (decryptedCharacters[i] + j));

                    return new String(encryptedCharacters);
                } catch (Exception ex) {
                    return OType.ERROR;
                }
            }

            @Override
            public String decrypt(String input, String key) {
                try {
                    int j = Integer.parseInt(key);
                    char[] encryptedCharacters = input.toCharArray();
                    char[] decryptedCharacters = new char[encryptedCharacters.length];

                    IntStream.range(0, encryptedCharacters.length).forEach(i -> decryptedCharacters[i] = (char) (encryptedCharacters[i] - j));

                    return new String(decryptedCharacters);
                } catch (Exception ex) {
                    return OType.ERROR;
                }

            }
        });
    }
}
