package de.byjoker.myjfql.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.byjoker.myjfql.util.JsonColumnParser;
import org.json.JSONPropertyIgnore;

public class CompiledColumn extends SimpleColumn {

    private String json;

    @Override
    public String toString() {
        return "CompiledColumn{" +
                "json='" + json + '\'' +
                '}';
    }

    @Override
    public void compile() {
        json = JsonColumnParser.stringify(this);
    }

    @JsonIgnore
    @JSONPropertyIgnore
    @Override
    public String json() {
        return json;
    }

}
