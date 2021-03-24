package org.jokergames.myjfql.encryption;

public class NoneEncryption extends Encryption {

    public NoneEncryption() {
        super("None", new Protocol() {
            @Override
            public String encrypt(String output, String key) {
                return output;
            }

            @Override
            public String decrypt(String input, String key) {
                return input;
            }
        });
    }
}
