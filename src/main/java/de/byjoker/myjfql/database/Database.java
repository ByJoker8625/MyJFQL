package de.byjoker.myjfql.database;

import de.byjoker.jfql.util.ID;
import de.byjoker.myjfql.exception.FileException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Database implements TableService {

    private String id;
    private List<Table> tables;
    private String name;

    public Database(String name) {
        this.id = ID.generateString().toString();
        this.name = name;
        this.tables = new ArrayList<>();
    }

    public Database(String id, String name) {
        this.id = id;
        this.name = name;
        this.tables = new ArrayList<>();
    }

    @Override
    public void createTable(Table table) {
        if (getTable(table.getName()) != null)
            throw new FileException("Table already exists in database!");

        saveTable(table);
    }

    @Override
    public void saveTable(Table table) {
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).getName().equals(table.getName())) {
                tables.set(i, table);
                return;
            }
        }

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

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Database database = (Database) o;
        return Objects.equals(id, database.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Database{" +
                "id='" + id + '\'' +
                ", tables=" + tables +
                ", name='" + name + '\'' +
                '}';
    }
}

