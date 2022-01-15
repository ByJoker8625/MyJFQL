package de.byjoker.myjfql.database;

import de.byjoker.myjfql.exception.FileException;
import de.byjoker.myjfql.util.IDGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapManagedDatabase implements Database {

    private final String name;
    private String id;
    private Map<String, Table> tables;

    public MapManagedDatabase(String name) {
        this.id = IDGenerator.generateMixed(16);
        this.name = name;
        this.tables = new HashMap<>();
    }

    public MapManagedDatabase(String id, String name) {
        this.id = id;
        this.name = name;
        this.tables = new HashMap<>();
    }

    @Override
    public void regenerateId() {
        this.id = IDGenerator.generateMixed(16);
    }

    @Override
    public void createTable(Table table) {
        if (getTable(table.getName()) != null)
            throw new FileException("Table already exists in database!");

        saveTable(table);
    }

    @Override
    public void saveTable(Table table) {
        tables.put(table.getName(), table);
    }

    @Override
    public boolean existsTable(String name) {
        return tables.containsKey(name);
    }

    @Override
    public void deleteTable(String name) {
        tables.remove(name);
    }

    @Override
    public Table getTable(String name) {
        return tables.get(name);
    }

    @Override
    public Collection<Table> getTables() {
        return tables.values();
    }

    public void setTables(Map<String, Table> tables) {
        this.tables = tables;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapManagedDatabase database = (MapManagedDatabase) o;
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
                ", tables=" + tables.values() +
                ", name='" + name + '\'' +
                '}';
    }
}

