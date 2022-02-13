package de.byjoker.myjfql.config;

public class Config {

    private ServerConfig server;
    private RegistryConfig registry;
    private String encryption;
    private boolean jline;

    public Config() {
    }

    public Config(ServerConfig server, RegistryConfig registry, String encryption, boolean jline) {
        this.server = server;
        this.registry = registry;
        this.encryption = encryption;
        this.jline = jline;
    }

    public boolean isJline() {
        return jline;
    }

    public void setJline(boolean jline) {
        this.jline = jline;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public ServerConfig getServer() {
        return server;
    }

    public void setServer(ServerConfig server) {
        this.server = server;
    }

    public RegistryConfig getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryConfig registry) {
        this.registry = registry;
    }
}
