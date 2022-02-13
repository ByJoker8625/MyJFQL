package de.byjoker.myjfql.config;

public class RegistryConfig {

    private String host;
    private boolean lookup;
    private boolean updates;
    private String output;

    public RegistryConfig() {
    }

    public RegistryConfig(String host, boolean lookup, boolean updates, String output) {
        this.host = host;
        this.lookup = lookup;
        this.updates = updates;
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

    public boolean isUpdates() {
        return updates;
    }

    public void setUpdates(boolean updates) {
        this.updates = updates;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
