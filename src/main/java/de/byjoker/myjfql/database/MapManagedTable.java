package de.byjoker.myjfql.database;

import de.byjoker.myjfql.lang.ColumnComparator;
import de.byjoker.myjfql.lang.SortingOrder;

import java.util.*;

public class MapManagedTable implements Table {

    private final String name;
    private Map<String, Column> columns;
    private Collection<String> structure;
    private String primary;

    public MapManagedTable(String name, List<String> structure, String primary) {
        this.name = name;
        this.structure = structure;
        this.primary = primary;
        this.columns = new HashMap<>();
    }

    @Override
    public void addColumn(Column column) {
        if (!column.containsOrNotNullItem(primary)) {
            return;
        }

        column.compile();
        columns.put(column.selectStringify(primary), column);
    }

    @Override
    public void removeColumn(String primary) {
        columns.remove(primary);
    }

    @Override
    public Column getColumn(String key) {
        return columns.get(key);
    }

    @Override
    public Collection<Column> getColumns() {
        return columns.values();
    }

    public void setColumns(Map<String, Column> columns) {
        this.columns = columns;
    }

    @Override
    public Collection<Column> getColumns(ColumnComparator comparator, SortingOrder order) {
        final List<Column> columns = new ArrayList<>(this.columns.values());
        columns.sort(comparator);

        if (order == SortingOrder.DESC)
            Collections.reverse(columns);

        return columns;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void clear() {
        this.columns = new HashMap<>();
    }

    @Override
    public Collection<String> getStructure() {
        return structure;
    }

    @Override
    public void setStructure(Collection<String> structure) {
        this.structure = structure;
        reindexColumns();
    }

    @Override
    public String getPrimary() {
        return primary;
    }

    @Override
    public void setPrimary(String primary) {
        this.primary = primary;
        reindexColumns();
    }

    public void reindexColumns() {
        final Collection<Column> columns = new ArrayList<>(this.columns.values());
        this.columns.clear();
        columns.forEach(this::addColumn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapManagedTable table = (MapManagedTable) o;
        return Objects.equals(name, table.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, structure, primary);
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", structure=" + structure +
                ", primary='" + primary + '\'' +
                ", columns=" + columns.values() +
                '}';
    }
}
