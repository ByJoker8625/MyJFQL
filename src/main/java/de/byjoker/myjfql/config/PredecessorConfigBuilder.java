package de.byjoker.myjfql.config;

import org.json.JSONObject;

import java.util.Arrays;

@ConfigFormer
public class PredecessorConfigBuilder extends ConfigBuilder {

    public PredecessorConfigBuilder() {
        super("=1.5.1", Arrays.asList("server", "data", "update"));
    }

    @Override
    public Config build(JSONObject json) {
        final JSONObject config = new ConfigDefaults().asJson();

        config.put("server", json.getJSONObject("server"));

        {
            JSONObject update = json.getJSONObject("update");
            config.put("updater", config.getJSONObject("updater").put("host", update.getString("host")));
            config.put("updater", config.getJSONObject("updater").put("autoUpdate", update.getBoolean("enabled")));
        }

        {
            final JSONObject data = json.getJSONObject("data");

            if (data.has("use.jline"))
                config.put("security", config.getJSONObject("security")
                        .put("jline", data.getBoolean("use.jline")));

            if (data.has("update.check"))
                config.put("updater", config.getJSONObject("updater")
                        .put("enabled", config.getBoolean("update.check")));

            if (data.has("show.connection.packet"))
                config.put("security", config.getJSONObject("security")
                        .put("showConnections", config.getBoolean("show.connection.packet")));
        }

        return new LatestConfigBuilder().build(config);
    }
}
