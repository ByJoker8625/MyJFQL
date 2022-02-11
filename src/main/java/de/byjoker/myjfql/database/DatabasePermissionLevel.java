package de.byjoker.myjfql.database;

public enum DatabasePermissionLevel {

    NONE(0),
    READ(1),
    READ_WRITE(2);

    private final int level;

    DatabasePermissionLevel(int level) {
        this.level = level;
    }

    public boolean can(DatabasePermissionLevel action) {
        return level >= action.level;
    }

}
