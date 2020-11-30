package de.jokergames.jfql.util;

import org.json.JSONObject;

import java.io.File;

/**
 * @author Janick
 */

public class ConfigHandler {

    private final FileFactory factory;
    private final JSONObject configuration;
    private boolean first = false;


    public ConfigHandler() {
        File file = new File("database");
        this.factory = new FileFactory();

        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File("module");

        if (!file.exists()) {
            file.mkdir();
        }

        file = new File("user");

        if (!file.exists()) {
            file.mkdir();
        }

        file = new File("script");

        if (!file.exists()) {
            file.mkdir();
            first = true;
        }

        file = new File("config.json");

        if (!file.exists()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("AutoUpdate", true);
            jsonObject.put("Port", 2291);
            jsonObject.put("Server", "http://jokergames.ddnss.de/lib/rest.json");

            factory.save(file, jsonObject);
        }

        this.configuration = factory.load(new File("config.json"));

        if (configuration.opt("AutoUpdate") == null)
            configuration.put("AutoUpdate", true);

        if (configuration.opt("Port") == null)
            configuration.put("Port", 2291);

        if (configuration.opt("Server") == null)
            configuration.put("Server", "http://jokergames.ddnss.de/lib/rest.json");

    }

    public JSONObject getConfig() {
        return configuration;
    }

    public boolean first() {
        return first;
    }

    public FileFactory getFactory() {
        return factory;
    }
}
