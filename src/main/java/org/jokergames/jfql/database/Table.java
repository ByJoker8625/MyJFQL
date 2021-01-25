package org.jokergames.jfql.database;

import org.jokergames.jfql.util.Sorter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class Table {

    private final String name;
    private List<String> structure;
    private String primary;
    private List<Column> columns;

    public Table(String name, List<String> structure, String primary) {
        this.name = name;
        this.structure = structure;
        this.primary = primary;
        this.columns = new ArrayList<>();
    }

    public void addColumn(Column column) {
        if (column.getContent(primary) == null) {
            return;
        }

        final Column col = getColumn(column.getContent(primary).toString());

        if (col == null) {
            columns.add(column);
            return;
        }

        removeColumn(column.getContent(primary).toString());

        column.setCreation(col.getCreation());
        columns.add(column);
    }

    public void removeColumn(String key) {
        columns.removeIf(col -> col.getContent().get(primary).toString().equals(key));
    }


    public Column getColumn(String key) {
        for (Column col : columns) {
            if (col.getContent().get(primary).toString().equals(key)) {
                return col;
            }
        }

        return null;
    }

    public List<Column> getColumns() {
        return getColumns(Sorter.Type.CREATION, Sorter.Order.ASC);
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Column> getColumns(Sorter.Type type, Sorter.Order order, String... strings) {
        switch (type) {
            case CREATION:
                return new Sorter().sortColumns(columns);
            case CUSTOM:
                return new Sorter().sortColumns(strings[0], columns, order);
        }

        return columns;
    }

    public String getName() {
        return name;
    }

    public List<String> getStructure() {
        return structure;
    }

    public void setStructure(List<String> structure) {
        this.structure = structure;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
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
