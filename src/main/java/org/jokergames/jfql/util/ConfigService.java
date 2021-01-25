package org.jokergames.jfql.util;

import org.jokergames.jfql.core.JFQL;
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
            jsonObject.put("Port", 2291);
            jsonObject.put("Server", "https://joker-games.org/lib/myjfql/rest.json");

            factory.save(file, jsonObject);
        }

        this.build();

        this.configuration = factory.loadJoin(new File("config.json"), new File("build.json"));

        if (configuration.opt("AutoUpdate") == null)
            configuration.put("AutoUpdate", true);

        if (configuration.opt("Port") == null)
            configuration.put("Port", 2291);

        if (configuration.opt("Server") == null)
            configuration.put("Server", "https://joker-games.org/lib/myjfql/rest.json");

    }

    public void build() {
        File file = new File("build.json");

        if (!file.exists()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Build", JFQL.getInstance().getVersion());
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
