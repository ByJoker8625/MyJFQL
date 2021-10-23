package de.byjoker.myjfql.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Database implements TableService {

    private final String name;
    private final List<Table> tables;

    public Database(String name) {
        this.name = name;
        this.tables = new ArrayList<>();
    }

    @Override
    public void createTable(Table table) {
        // TODO: 23.10.2021
    }

    @Override
    public void saveTable(final Table table) {
        deleteTable(table.getName());
        tables.add(table);
    }

    @Override
    public boolean existsTable(String name) {
        return tables.stream().anyMatch(table -> table.getName().equals(name));
    }

    @Override
    public void deleteTable(String name) {
        tables.removeIf(table -> table.getName().equals(name));
    }

    @Override
    public Table getTable(String name) {
        return tables.stream().filter(table -> table.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public List<Table> getTables() {
        return tables;
    }

    public String getName() {
        return name;
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

