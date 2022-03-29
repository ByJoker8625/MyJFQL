package de.byjoker.myjfql.database;

import de.byjoker.myjfql.exception.TableException;
import de.byjoker.myjfql.util.IDGenerator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SimpleDatabase implements Database {

    private final String name;
    private final Map<String, Table> tables;
    private String id;
    private DatabaseType type;

    public SimpleDatabase(String name) {
        this.id = IDGenerator.generateMixed(16);
        this.name = name;
        this.tables = new HashMap<>();
        this.type = DatabaseType.SPLIT;
    }

    public SimpleDatabase(String id, String name, DatabaseType type) {
        this.id = id;
        this.name = name;
        this.tables = new HashMap<>();
        this.type = type;
    }

    @Override
    public void regenerateId() {
        this.id = IDGenerator.generateMixed(16);
    }

    @Override
    public void createTable(Table table) {
        if (getTable(table.getName()) != null)
            throw new TableException("Table already exists in database!");

        saveTable(table);
    }

    @Override
    public void saveTable(@NotNull Table table) {
        tables.put(table.getName(), table);
    }

    @Override
    public boolean existsTable(@NotNull String name) {
        return tables.containsKey(name);
    }

    @Override
    public void deleteTable(@NotNull String name) {
        if (type == DatabaseType.SPLIT) {
            new File("database/" + id + "/" + name).delete();
        }

        tables.remove(name);
    }

    @Override
    public void reformat(@NotNull DatabaseType type, DatabaseService service) {
        service.deleteDatabase(id);
        this.type = type;
        service.createDatabase(this);
    }

    @Override
    public Table getTable(@NotNull String name) {
        return tables.get(name);
    }

    @NotNull
    @Override
    public Collection<Table> getTables() {
        return tables.values();
    }

    @NotNull
    @Override
    public DatabaseType getType() {
        return type;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "SimpleDatabase{" +
                "name='" + name + '\'' +
                ", tables=" + tables +
                ", type=" + type +
                ", id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleDatabase that = (SimpleDatabase) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
