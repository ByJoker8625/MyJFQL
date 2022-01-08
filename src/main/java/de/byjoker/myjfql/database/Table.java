package de.byjoker.myjfql.database;

import de.byjoker.myjfql.util.Sorter;

import java.util.*;

public class Table implements ColumnHandler {

    private final String name;
    private Map<String, Column> columns;
    private Collection<String> structure;
    private String primary;

    public Table(String name, List<String> structure, String primary) {
        this.name = name;
        this.structure = structure;
        this.primary = primary;
        this.columns = new HashMap<>();
    }

    @Override
    public void addColumn(Column column) {
        if (String.valueOf(column.getContent(primary)).equals("null")) {
            return;
        }

        columns.put(column.getContent(primary).toString(), column);
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
        return getColumns(Sorter.Type.CREATION, Sorter.Order.ASC);
    }

    public void setColumns(Map<String, Column> columns) {
        this.columns = columns;
    }

    @Override
    public Collection<Column> getColumns(Sorter.Type state, Sorter.Order order, String... sortedBy) {
        switch (state) {
            case CREATION:
                return Sorter.sortColumns(columns.values());
            case CUSTOM:
                return Sorter.sortColumns(sortedBy[0], columns.values(), order);
        }

        return columns.values();
    }

    @Override
    public String getName() {
        return name;
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
        Table table = (Table) o;
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
