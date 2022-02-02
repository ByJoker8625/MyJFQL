package de.byjoker.myjfql.database;

import de.byjoker.myjfql.exception.FileException;
import de.byjoker.myjfql.util.IDGenerator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DatabaseImpl implements Database {

    private final String name;
    private final Map<String, Table> tables;
    private DatabaseType type;
    private String id;

    public DatabaseImpl(String name) {
        this.id = IDGenerator.generateMixed(16);
        this.name = name;
        this.tables = new HashMap<>();
        this.type = DatabaseType.SPLIT_STORAGE_TARGET;
    }

    public DatabaseImpl(String id, String name, DatabaseType type) {
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
        if (type == DatabaseType.SPLIT_STORAGE_TARGET) {
            try {
                FileUtils.deleteDirectory(new File("database/" + id + "/" + name));
            } catch (IOException ex) {
                throw new FileException(ex);
            }
        }

        tables.remove(name);
    }

    @Override
    public void reformat(DatabaseType type, DatabaseService service) {
        service.deleteDatabase(id);
        this.type = type;
        service.createDatabase(this);
    }

    @Override
    public Table getTable(String name) {
        return tables.get(name);
    }

    @Override
    public Collection<Table> getTables() {
        return tables.values();
    }

    @Override
    public DatabaseType getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

}

