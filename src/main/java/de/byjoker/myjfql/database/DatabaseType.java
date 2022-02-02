package de.byjoker.myjfql.database;

import java.util.Arrays;
import java.util.List;

public enum DatabaseType {

    SINGLE_STORAGE_TARGET("FILE", "SINGLE"),
    SPLIT_STORAGE_TARGET("FOLDER", "SPLIT", "MULTIPLE");

    private final List<String> aliases;

    DatabaseType(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }

    public static DatabaseType likeDatabaseType(String like) {
        return Arrays.stream(DatabaseType.values()).filter(databaseType -> databaseType.aliases.contains(like.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    public List<String> getAliases() {
        return aliases;
    }
}
