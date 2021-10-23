package de.byjoker.myjfql.config;

import de.byjoker.myjfql.util.FileFactory;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class ConfigService {

    private final FileFactory factory;
    private JSONObject configuration;
    private JSONObject rawConfiguration;

    public ConfigService() {
        this.factory = new FileFactory();
        this.configuration = null;
        this.rawConfiguration = null;

        load();
    }

    public void load() {
        File file = new File("database");

        if (!file.exists())
            file.mkdir();

        file = new File("user");

        if (!file.exists())
            file.mkdir();

        file = new File("backup");

        if (!file.exists())
            file.mkdir();

        file = new File("config.json");

        if (!file.exists()) {
            final JSONObject configuration = new JSONObject();

            {
                final JSONObject updateConfig = new JSONObject();
                updateConfig.put("enabled", false);
                updateConfig.put("host", "https://joker-games.org/api/myjfql.json");
                configuration.put("update", updateConfig);
            }

            {
                final JSONObject serverConfig = new JSONObject();
                serverConfig.put("enabled", true);
                serverConfig.put("port", 2291);
                configuration.put("server", serverConfig);
            }

            configuration.put("data", new HashMap<>());

            factory.saveJSONFormatted(file, configuration);
        }

        JSONObject configuration = factory.load(file);
        this.rawConfiguration = configuration;

        if (isNonCompatibleConfiguration(configuration)) {
            configuration = buildCompatibleConfiguration(configuration);
        }

        this.configuration = configuration;
    }

    public Config getConfig() {
        return new DBMSConfig(configuration);
    }

    @Deprecated
    public JSONObject getConfiguration() {
        return configuration;
    }

    public JSONObject getRawConfiguration() {
        return rawConfiguration;
    }

    public FileFactory getFactory() {
        return factory;
    }

    public boolean isNonCompatibleConfiguration(final JSONObject configuration) {
        return (!configuration.has("server") && !configuration.has("update"))
                && (configuration.has("Port") && configuration.has("UpdateServer") && configuration.has("AutoUpdate"));
    }

    private JSONObject buildCompatibleConfiguration(final JSONObject incompatibleConfiguration) {
        final JSONObject configuration = new JSONObject();

        {
            final JSONObject updateConfig = new JSONObject();

            if (incompatibleConfiguration.has("AutoUpdate")) {
                updateConfig.put("enabled", incompatibleConfiguration.getBoolean("AutoUpdate"));
            }

            if (incompatibleConfiguration.has("UpdateServer")) {
                updateConfig.put("host", incompatibleConfiguration.getString("UpdateServer"));
            }

            configuration.put("update", updateConfig);
        }

        {
            final JSONObject serverConfig = new JSONObject();

            if (incompatibleConfiguration.has("Port")) {
                serverConfig.put("port", incompatibleConfiguration.getInt("Port"));
            }

            serverConfig.put("enabled", true);
            configuration.put("server", serverConfig);
        }

        return configuration;
    }

}
