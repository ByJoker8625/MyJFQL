package de.byjoker.myjfql.database;

import de.byjoker.myjfql.util.Sorter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Table implements ColumnHandler {

    private String name;
    private List<Column> columns;
    private List<String> structure;
    private String primary;

    public Table(String name, List<String> structure, String primary) {
        this.name = name;
        this.structure = structure;
        this.primary = primary;
        this.columns = new ArrayList<>();
    }

    @Override
    public void addColumn(Column column) {
        if (column.getContent(primary) == null) {
            return;
        }

        for (int i = 0; i < columns.size(); i++) {
            final Column col = columns.get(i);

            if (col.getContent(primary).equals(column.getContent(primary))) {
                column.setCreation(col.getCreation());
                columns.set(i, column);
                return;
            }
        }

        columns.add(column);
    }

    @Override
    public void removeColumn(String key) {
        columns.removeIf(col -> col.getContent(primary).toString().equals(key));
    }

    @Override
    public Column getColumn(String key) {
        return columns.stream().filter(col -> col.getContent(primary).toString().equals(key)).findFirst().orElse(null);
    }

    @Override
    public List<Column> getColumns() {
        return getColumns(Sorter.Type.CREATION, Sorter.Order.ASC);
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public List<Column> getColumns(Sorter.Type state, Sorter.Order order, String... strings) {
        switch (state) {
            case CREATION:
                return Sorter.sortColumns(columns);
            case CUSTOM:
                return Sorter.sortColumns(strings[0], columns, order);
        }

        return columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getStructure() {
        return structure;
    }

    public void setStructure(final List<String> structure) {
        this.structure = structure;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(final String primary) {
        this.primary = primary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(name, table.name) &&
                Objects.equals(structure, table.structure) &&
                Objects.equals(primary, table.primary);
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
                ", columns=" + columns +
                '}';
    }
}
