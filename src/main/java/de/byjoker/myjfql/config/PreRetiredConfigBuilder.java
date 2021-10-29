package de.byjoker.myjfql.config;

import org.json.JSONObject;

import java.util.Arrays;

@ConfigHandler
public class PreRetiredConfigBuilder extends ConfigBuilder {

    public PreRetiredConfigBuilder() {
        super("<1.4.4", Arrays.asList("UpdateServer", "AutoUpdate", "Port"));
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
