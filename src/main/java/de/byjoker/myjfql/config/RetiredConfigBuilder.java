package de.byjoker.myjfql.config;

import org.json.JSONObject;

import java.util.Arrays;

@ConfigHandler
public class RetiredConfigBuilder extends ConfigBuilder {

    public RetiredConfigBuilder() {
        super("<1.5.1", Arrays.asList("SecondaryBackup", "UpdateServer", "AutoUpdate", "Port"));
    }

    @Override
    public Config build(JSONObject json) {
        final JSONObject config = new ConfigDefaults().asJson();

        config.put("updater", config.getJSONObject("updater")
                .put("autoUpdate", json.getBoolean("AutoUpdate")));

        config.put("updater", config.getJSONObject("updater")
                .put("host", json.getString("UpdateServer")));

        config.put("server", config.getJSONObject("server")
                .put("port", json.getInt("Port")));

        return new LatestConfigBuilder().build(config);
    }
}
