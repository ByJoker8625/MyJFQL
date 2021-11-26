package de.byjoker.myjfql.config;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public abstract class ConfigBuilder {

    private final String target;
    private final List<String> identifiers;

    public ConfigBuilder(String target, List<String> identifiers) {
        this.target = target;
        this.identifiers = identifiers;
    }

    public abstract Config build(JSONObject json);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigBuilder that = (ConfigBuilder) o;
        return Objects.equals(target, that.target) && Objects.equals(identifiers, that.identifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, identifiers);
    }

    @Override
    public String toString() {
        return "ConfigBuilder{" +
                "target='" + target + '\'' +
                ", identifiers=" + identifiers +
                '}';
    }

    public String getTarget() {
        return target;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }
}
