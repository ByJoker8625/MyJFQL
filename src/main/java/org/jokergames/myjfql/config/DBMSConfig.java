package org.jokergames.myjfql.config;

import org.json.JSONObject;

public class DBMSConfig implements Config {

    private final DefaultConfig defaultConfig = new DefaultConfig();
    private final JSONObject configuration;

    public DBMSConfig(JSONObject configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getUpdateHost() {
        if (!configuration.has("update"))
            return defaultConfig.getUpdateHost();

        final JSONObject update = configuration.getJSONObject("update");

        if (!update.has("host"))
            return defaultConfig.getUpdateHost();

        return update.getString("host");
    }

    @Override
    public int getServerPort() {
        if (!configuration.has("server"))
            return defaultConfig.getServerPort();

        final JSONObject server = configuration.getJSONObject("server");

        if (!server.has("port"))
            return defaultConfig.getServerPort();

        return server.getInt("port");
    }

    @Override
    public boolean enabledServer() {
        if (!configuration.has("server"))
            return defaultConfig.enabledServer();

        final JSONObject server = configuration.getJSONObject("server");

        if (!server.has("enabled"))
            return defaultConfig.enabledServer();

        return server.getBoolean("enabled");
    }

    @Override
    public boolean enabledUpdates() {
        if (!configuration.has("update"))
            return defaultConfig.enabledUpdates();

        final JSONObject update = configuration.getJSONObject("update");

        if (!update.has("enabled"))
            return defaultConfig.enabledUpdates();

        return update.getBoolean("enabled");
    }

    @Override
    public boolean enabledJLine() {
        if (!configuration.has("data"))
            return defaultConfig.enabledJLine();

        final JSONObject data = configuration.getJSONObject("data");

        if (!data.has("use.jline"))
            return defaultConfig.enabledJLine();

        return data.getBoolean("use.jline");
    }

    @Override
    public boolean updateCheck() {
        if (!configuration.has("data"))
            return defaultConfig.updateCheck();

        final JSONObject data = configuration.getJSONObject("data");

        if (!data.has("update.check"))
            return defaultConfig.updateCheck();

        return data.getBoolean("update.check");
    }

    @Override
    public boolean showConnectionPacket() {
        if (!configuration.has("data"))
            return defaultConfig.showConnectionPacket();

        final JSONObject data = configuration.getJSONObject("data");

        if (!data.has("show.connection.packet"))
            return defaultConfig.showConnectionPacket();

        return data.getBoolean("show.connection.packet");
    }

    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public String toString() {
        return configuration.toString();
    }
}
