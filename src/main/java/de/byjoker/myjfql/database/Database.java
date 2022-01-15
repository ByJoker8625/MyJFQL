package de.byjoker.myjfql.database;

import java.util.Collection;

public interface Database {

    void regenerateId();

    void createTable(Table table);

    void saveTable(Table table);

    boolean existsTable(String name);

    void deleteTable(String name);

    Table getTable(String name);

    Collection<Table> getTables();

    String getName();

    String getId();

}
