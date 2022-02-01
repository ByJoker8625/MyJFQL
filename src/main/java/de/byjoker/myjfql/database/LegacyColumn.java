package de.byjoker.myjfql.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.byjoker.myjfql.util.JsonColumnParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONPropertyIgnore;

import java.util.HashMap;
import java.util.Map;

public class LegacyColumn extends SimpleColumn {

    public LegacyColumn(Map<String, Object> content, long createdAt) {
        super(content, createdAt);
    }

    public LegacyColumn() {
        super(new HashMap<>(), System.currentTimeMillis());
    }

    /**
     * I don't know why case this is already in SimpleColumn but kotlin wants that
     */

    @Override
    public void applyContent(@NotNull Map<String, Object> content) {
        getContent().putAll(content);
    }

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
