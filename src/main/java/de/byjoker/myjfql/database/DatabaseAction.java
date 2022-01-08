package de.byjoker.myjfql.database;

public enum DatabaseAction {

    NONE(0),
    READ(1),
    READ_WRITE(2);

    public final int level;

    DatabaseAction(int level) {
        this.level = level;
    }

    public boolean can(DatabaseAction action) {
        return level >= action.level;
    }

}
