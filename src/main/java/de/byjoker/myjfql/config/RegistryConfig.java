package de.byjoker.myjfql.config;

public class RegistryConfig {

    private String host;
    private boolean lookup;
    private boolean autoUpdates;
    private String output;

    public RegistryConfig() {
    }

    public RegistryConfig(String host, boolean lookup, boolean autoUpdates, String output) {
        this.host = host;
        this.lookup = lookup;
        this.autoUpdates = autoUpdates;
        this.output = output;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isLookup() {
        return lookup;
    }

    public void setLookup(boolean lookup) {
        this.lookup = lookup;
    }

    public boolean issAutoUpdates() {
        return autoUpdates;
    }

    public void setAutoUpdates(boolean updates) {
        this.autoUpdates = updates;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
