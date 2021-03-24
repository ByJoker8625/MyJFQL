package org.jokergames.myjfql.encryption;

import java.util.Objects;

public class Encryption {

    private final String name;
    private Protocol protocol;
    private String key;

    public Encryption(String name, Protocol protocol) {
        this.name = name;
        this.protocol = protocol;
        this.key = null;
    }

    public String getName() {
        return name;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Encryption{" +
                "name='" + name + '\'' +
                ", protocol=" + protocol +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Encryption that = (Encryption) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public interface Protocol {
        String encrypt(String output, String key);

        String decrypt(String input, String key);
    }

    public static class OType {
        public static String ERROR = "\\nn\n\n\nÜNÜL\\LTH\\ISIS\\ANERR\\OR\\CODEÜödPW\\\\nÜÖÖÜ\\\\nüpLPLP\\\\näPLÜLÖÄpüü\\\\\\\\nÖÄ\n\nLÄ\n\\nnÖL\\nn\\\nnn\n\n\nP\n";
    }


}
