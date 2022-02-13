package de.byjoker.myjfql.config;

public class ConfigDefaults extends Config {

    @Override
    public String getEncryption() {
        return "NONE";
    }

    @Override
    public boolean isJline() {
        return true;
    }

    @Override
    public ServerConfig getServer() {
        return new ServerConfig(true, 2291);
    }

    @Override
    public RegistryConfig getRegistry() {
        return new RegistryConfig("https://cdn.byjoker.de/myjfql/myjfql.json", true, false, "MyJFQL.jar");
    }
}
