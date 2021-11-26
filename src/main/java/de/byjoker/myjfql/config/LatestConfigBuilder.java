package de.byjoker.myjfql.config;

import org.json.JSONObject;

import java.util.Arrays;

@ConfigHandler
public class LatestConfigBuilder extends ConfigBuilder {

    public LatestConfigBuilder() {
        super("=1.5.2", Arrays.asList("server", "security", "updater"));
    }

    @Override
    public Config build(JSONObject json) {
        return new Config() {
            @Override
            public int port() {
                return json.getJSONObject("server").getInt("port");
            }

            @Override
            public boolean server() {
                return json.getJSONObject("server").getBoolean("enabled");
            }

            @Override
            public boolean updates() {
                return json.getJSONObject("updater").getBoolean("enabled");
            }

            @Override
            public boolean autoUpdate() {
                return json.getJSONObject("updater").getBoolean("autoUpdate");
            }

            @Override
            public boolean crossTokenRequests() {
                return json.getJSONObject("security").getBoolean("crossTokenRequests");
            }

            @Override
            public boolean memorySessions() {
                return json.getJSONObject("security").getBoolean("memorySessions");
            }

            @Override
            public boolean onlyManualSessionControl() {
                return json.getJSONObject("security").getBoolean("onlyManualSessionControl");
            }

            @Override
            public String updateHost() {
                return json.getJSONObject("updater").getString("host");
            }

            @Override
            public String encryption() {
                return json.getJSONObject("security").getString("encryption");
            }

            @Override
            public boolean jline() {
                return json.getJSONObject("security").getBoolean("jline");
            }

            @Override
            public boolean showConnections() {
                return json.getJSONObject("security").getBoolean("showConnections");
            }

            @Override
            public boolean showQueries() {
                return json.getJSONObject("security").getBoolean("showQueries");
            }

            @Override
            public JSONObject asJson() {
                return json;
            }
        };
    }
}
