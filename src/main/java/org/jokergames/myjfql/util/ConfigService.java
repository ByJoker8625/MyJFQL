package org.jokergames.myjfql.util;

import org.jokergames.myjfql.core.MyJFQL;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDate;

/**
 * @author Janick
 */

public class ConfigService {

    private final FileFactory factory;
    private final JSONObject configuration;
    private boolean first = false;


    public ConfigService() {
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
            jsonObject.put("Uppercase", false);
            jsonObject.put("Port", 2291);
            jsonObject.put("UpdateServer", "https://joker-games.org/lib/myjfql/rest.json");

            factory.save(file, jsonObject);
        }

        this.build();

        this.configuration = factory.loadJoin(new File("config.json"), new File("build.json"));

        if (configuration.opt("AutoUpdate") == null)
            configuration.put("AutoUpdate", true);

        if (configuration.opt("Uppercase") == null)
            configuration.put("Uppercase", false);

        if (configuration.opt("Port") == null)
            configuration.put("Port", 2291);

        if (configuration.opt("UpdateServer") == null)
            configuration.put("UpdateServer", "https://joker-games.org/lib/myjfql/rest.json");

    }

    public void build() {
        File file = new File("build.json");

        if (!file.exists()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Build", MyJFQL.getInstance().getVersion());
            jsonObject.put("Date", LocalDate.now());

            factory.save(file, jsonObject);
        }
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
