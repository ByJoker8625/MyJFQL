package de.byjoker.myjfql.database;

import java.util.List;

public interface TableService {

    void createTable(Table table);

    void saveTable(Table table);

    boolean existsTable(String name);

    void deleteTable(String name);

    Table getTable(String name);

    List<Table> getTables();

}
