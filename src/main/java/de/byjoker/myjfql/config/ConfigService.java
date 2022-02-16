package de.byjoker.myjfql.config;

public interface ConfigService {

    void buildDefaults();

    void mkdirs();

    Config load();

}
