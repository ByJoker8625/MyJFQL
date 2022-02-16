package de.byjoker.myjfql.config;

public class ServerConfig {

    private boolean enabled;
    private int port;

    public ServerConfig() {
    }

    public ServerConfig(boolean enabled, int port) {
        this.enabled = enabled;
        this.port = port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
