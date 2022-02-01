package de.byjoker.myjfql.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.byjoker.myjfql.util.JsonColumnParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONPropertyIgnore;

import java.util.HashMap;
import java.util.Map;

public class RelationalColumn extends SimpleColumn {

    private String json;

    public RelationalColumn(Map<String, Object> content, long createdAt) {
        super(content, createdAt);
        this.json = "{}";
    }

    public RelationalColumn() {
        super(new HashMap<>(), System.currentTimeMillis());
        this.json = "{}";
    }

    /**
     * I don't know why case this is already in SimpleColumn but kotlin wants that
     */

    @Override
    public void applyContent(@NotNull Map<String, Object> content) {
        getContent().putAll(content);
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
