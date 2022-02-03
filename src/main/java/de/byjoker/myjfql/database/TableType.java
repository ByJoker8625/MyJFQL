package de.byjoker.myjfql.database;

import java.util.Arrays;
import java.util.List;

public enum TableType {

    RELATIONAL("RELATIONAL", "REL", "SQL", "THE_THING_I_EVER_USED_BEFORE"),
    DOCUMENT("DOCUMENT", "DOC", "NOSQL", "JSON");

    private final List<String> aliases;

    TableType(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }

    public static TableType likeTableType(String like) {
        return Arrays.stream(TableType.values()).filter(tableType -> tableType.aliases.contains(like.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
