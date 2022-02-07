package de.byjoker.myjfql.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.byjoker.myjfql.util.TableEntryParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONPropertyIgnore;

import java.util.HashMap;
import java.util.Map;

public class RelationalTableEntry extends SimpleTableEntry {

    private String json;

    public RelationalTableEntry(Map<String, Object> content, long createdAt) {
        super(content, createdAt);
        this.json = "{}";
    }

    public RelationalTableEntry() {
        super(new HashMap<>(), System.currentTimeMillis());
        this.json = "{}";
    }

    public RelationalTableEntry(TableEntry tableEntry) {
        super(tableEntry.getContent(), tableEntry.getCreatedAt());
    }

    /**
     * I don't know why case this is already in SimpleTableEntry but kotlin wants that
     */

    @Override
    public void applyContent(@NotNull Map<String, Object> content) {
        getContent().putAll(content);
    }

    @Override
    public void compile() {
        json = TableEntryParser.stringify(this);
    }

    @JsonIgnore
    @JSONPropertyIgnore
    @Override
    public String json() {
        return json;
    }

}
