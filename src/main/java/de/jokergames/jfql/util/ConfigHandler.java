package de.jokergames.jfql.util;

import org.json.JSONObject;

import java.io.File;

/**
 * @author Janick
 */

public class ConfigHandler {

    private final FileFactory factory;
    private boolean crt = false;

    public ConfigHandler() {
        File file = new File("database");
        this.factory = new FileFactory();

        if (!file.exists()) {
            file.mkdirs();
            crt = true;
        }

        file = new File("module");

        if (!file.exists()) {
            file.mkdir();
        }

        file = new File("user");

        if (!file.exists()) {
            file.mkdir();
        }

        file = new File("config.json");

        if (!file.exists()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("AutoUpdate", true);
            jsonObject.put("Port", 2291);
            jsonObject.put("Server", "http://jokergames.ddnss.de/lib/rest.json");

            factory.save(file, jsonObject);
        }

    }

    public JSONObject getConfig() {
        return factory.load(new File("config.json"));
    }

    public boolean isCrt() {
        return crt;
    }

    public FileFactory getFactory() {
        return factory;
    }
}
