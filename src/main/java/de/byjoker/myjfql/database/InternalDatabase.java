package de.byjoker.myjfql.database;

import de.byjoker.myjfql.exception.DatabaseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalDatabase implements Database {

    private final Map<String, Table> tables;

    public InternalDatabase(List<Table> tables) {
        this.tables = new HashMap<>();
        tables.forEach(table -> this.tables.put(table.getName(), table));
    }

    @NotNull
    @Override
    public Collection<Table> getTables() {
        return tables.values();
    }

    @NotNull
    @Override
    public DatabaseType getType() {
        return DatabaseType.INTERNAL;
    }

    @NotNull
    @Override
    public String getName() {
        return "internal";
    }

    @NotNull
    @Override
    public String getId() {
        return "internal";
    }

    @Override
    public void regenerateId() {
        throw new DatabaseException("The id of the internal database cannot be changed!");
    }

    @Override
    public void createTable(@NotNull Table table) {
        throw new DatabaseException("System-internal tables cannot be created manually!");
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
        throw new DatabaseException("System-internal tables cannot be deleted!");
    }

    @Override
    public void reformat(@NotNull DatabaseType type, @NotNull DatabaseService service) {
        throw new DatabaseException("The storage-structure of the internal database cannot be changed!");
    }

    @Nullable
    @Override
    public Table getTable(@NotNull String name) {
        return tables.get(name);
    }

}
