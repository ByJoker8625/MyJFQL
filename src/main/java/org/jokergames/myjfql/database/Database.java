package org.jokergames.myjfql.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Janick
 */

public class Database {

    private final String name;
    private List<Table> tables;

    public Database(final String name) {
        this.name = name;
        this.tables = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(final List<Table> tables) {
        this.tables = tables;
    }

    public void addTable(final Table table) {
        removeTable(table.getName());
        tables.add(table);
    }

    public boolean isCreated(String name) {
        return tables.stream().anyMatch(table -> table.getName().equals(name));
    }

    public void removeTable(String name) {
        tables.removeIf(table -> table.getName().equals(name));
    }

    public Table getTable(final String name) {
        return tables.stream().filter(table -> table.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Database database = (Database) o;
        return Objects.equals(name, database.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "DataBase{" +
                "name='" + name + '\'' +
                ", tables=" + tables +
                '}';
    }

}

