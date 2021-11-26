package de.byjoker.myjfql.database;

import de.byjoker.myjfql.util.Sorter;

import java.util.List;

public interface ColumnHandler {

    void addColumn(Column column);

    void removeColumn(String identifier);

    Column getColumn(String identifier);

    List<Column> getColumns();

    List<Column> getColumns(Sorter.Type type, Sorter.Order order, String... strings);

}
