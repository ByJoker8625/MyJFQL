package de.byjoker.myjfql.database;

public enum DatabaseActionPerformType {

    NONE(0),
    READ(1),
    READ_WRITE(2);

    private final int level;

    DatabaseActionPerformType(int level) {
        this.level = level;
    }

    public boolean can(DatabaseActionPerformType action) {
        return level >= action.level;
    }

}
