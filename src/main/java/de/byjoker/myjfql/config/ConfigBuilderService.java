package de.byjoker.myjfql.config;

import org.json.JSONObject;

import java.util.List;

public interface ConfigBuilderService {

    void registerConfigBuilder(ConfigBuilder configBuilder);

    void unregisterConfigBuilder(ConfigBuilder configBuilder);

    void searchConfigBuilders(String path);

    ConfigBuilder getConfigBuilder(JSONObject json);

    List<ConfigBuilder> getConfigBuilders();

}
