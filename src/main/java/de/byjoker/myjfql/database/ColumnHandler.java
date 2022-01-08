package de.byjoker.myjfql.database;

import de.byjoker.myjfql.util.Sorter;

import java.util.Collection;

public interface ColumnHandler {

    void addColumn(Column column);

    void removeColumn(String identifier);

    Column getColumn(String identifier);

    Collection<Column> getColumns();

    Collection<Column> getColumns(Sorter.Type type, Sorter.Order order, String... sortedBy);

    Collection<String> getStructure();

    void setStructure(Collection<String> structure);

    String getPrimary();

    void setPrimary(String primary);

    String getName();

}
