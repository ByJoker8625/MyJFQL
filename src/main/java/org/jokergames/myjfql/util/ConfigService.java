package org.jokergames.myjfql.util;

import org.json.JSONObject;

import java.io.File;

public class ConfigService {

    private final FileFactory factory;
    private JSONObject configuration;
    private boolean first;

    public ConfigService() {
        this.factory = new FileFactory();
        this.configuration = null;
        this.first = false;

        load();
    }

    public void load() {
        File file = new File("database");

        if (!file.exists())
            file.mkdirs();

        file = new File("user");

        if (!file.exists())
            file.mkdirs();

        file = new File("backup");

        if (!file.exists())
            file.mkdir();

        file = new File("config.json");

        if (!file.exists()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("AutoUpdate", true);
            jsonObject.put("Port", 2291);
            jsonObject.put("SecondaryBackup", false);
            jsonObject.put("UpdateServer", "https://joker-games.org/lib/myjfql/rest.json");

            factory.save(file, jsonObject);
            first = true;
        }

        this.configuration = factory.load(file);
    }

    public boolean isFirst() {
        return first;
    }

    public JSONObject getConfiguration() {
        return configuration;
    }

    public FileFactory getFactory() {
        return factory;
    }
}
