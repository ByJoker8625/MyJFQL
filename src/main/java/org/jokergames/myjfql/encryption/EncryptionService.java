package org.jokergames.myjfql.encryption;

import org.jokergames.myjfql.exception.InternalException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EncryptionService {

    private final List<Encryption> encryptions;
    private final Map<String, String> encryptionKeys;
    private final JSONObject encryption;
    private Encryption defaultEncryption;

    public EncryptionService(JSONObject encryption) {
        this.encryption = encryption;
        this.encryptions = Arrays.asList(new NoneEncryption(), new DDPEncryption());
        this.defaultEncryption = encryptions.stream().filter(encryption1 -> encryption1.getName().equalsIgnoreCase(encryption.getString("default"))).findFirst().orElse(null);

        if (defaultEncryption == null) {
            throw new InternalException("Encryption '" + encryption.getString("default") + "' not found!");
        }

        this.encryptionKeys = new HashMap<>();
        encryption.keySet().stream().filter(key -> !key.equalsIgnoreCase("default")).forEach(key -> encryptionKeys.put(key.toLowerCase(), encryption.getString(key)));
    }

    public void addEncryption(Encryption encryption) {
        encryptions.add(encryption);
    }

    public void removeEncryption(Encryption encryption) {
        encryptions.remove(encryption);
    }

    public Encryption getEncryption(String name) {
        Encryption encryption = encryptions.stream().filter(encryption1 -> encryption1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);

        if (encryption == null)
            return null;

        encryption.setKey(encryptionKeys.get(encryption.getName().toLowerCase()));
        return encryption;
    }

    public Encryption getDefaultEncryption() {
        return defaultEncryption;
    }

    public void setDefaultEncryption(Encryption defaultEncryption) {
        this.defaultEncryption = defaultEncryption;
    }

    public Map<String, String> getEncryptionKeys() {
        return encryptionKeys;
    }

    public List<Encryption> getEncryptions() {
        return encryptions;
    }
}
