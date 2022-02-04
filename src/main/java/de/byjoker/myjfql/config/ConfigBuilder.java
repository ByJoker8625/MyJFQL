package de.byjoker.myjfql.config;

import org.json.JSONObject;

import java.util.List;

public abstract class ConfigBuilder {

    private final String target;
    private final List<String> identifiers;

    public ConfigBuilder(String target, List<String> identifiers) {
        this.target = target;
        this.identifiers = identifiers;
    }

    public abstract Config build(JSONObject json);

    public String getTarget() {
        return target;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }
}
