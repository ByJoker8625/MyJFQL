package de.byjoker.myjfql.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.byjoker.myjfql.util.JsonColumnParser;
import org.json.JSONPropertyIgnore;

public class LegacyColumn extends SimpleColumn {

    @Deprecated
    @Override
    public void compile() {
    }

    @JsonIgnore
    @JSONPropertyIgnore
    @Override
    public String json() {
        return JsonColumnParser.stringify(this);
    }

}
