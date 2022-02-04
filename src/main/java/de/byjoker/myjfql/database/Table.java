package de.byjoker.myjfql.database;

import de.byjoker.myjfql.lang.ColumnComparator;
import de.byjoker.myjfql.util.SortingOrder;

import java.util.Collection;

public interface Table {

    void addColumn(Column column);

    void removeColumn(String identifier);

    Column getColumn(String identifier);

    Collection<Column> getColumns();

    Collection<Column> getColumns(ColumnComparator comparator, SortingOrder order);

    Collection<String> getStructure();

    void setStructure(Collection<String> structure);

    String getPrimary();

    void setPrimary(String primary);

    Table reformat(TableType type, String... parameters);

    TableType getType();

    String getName();

    void clear();
}
