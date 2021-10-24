package de.byjoker.myjfql.config;

public interface ConfigService extends ConfigBuilderService {

    void load();

    void buildRequirements();

    Config getConfig();

}
