package de.byjoker.myjfql.database;

import de.byjoker.myjfql.util.Json;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class DatabasesTable extends InternalTable {

    private final DatabaseService databaseService;

    public DatabasesTable(DatabaseService databaseService) {
        super("databases", "databases", Arrays.asList("_id", "name", "type", "tables"), "_id");
        this.databaseService = databaseService;
    }

    @Override
    public void addEntry(TableEntry tableEntry) {
        if (!(tableEntry instanceof Document)) {
            return;
        }

        System.out.println(tableEntry);
    }

    @Override
    public void removeEntry(String identifier) {
        databaseService.deleteDatabase(identifier);
    }

    @Override
    public Collection<TableEntry> getEntries() {
        return databaseService.getDatabases().stream().map(database -> {
                    return new Document().append("_id", database.getId()).append("name", database.getName()).append("type", database.getType()).append("tables", Json.stringify(database.getTables().stream().map(TableRepresentation::new).collect(Collectors.toList())));
                }).
                collect(Collectors.toList());
    }

    @Override
    public void clear() {
        databaseService.getDatabases().forEach(database -> databaseService.deleteDatabase(database.getId()));
    }

    @Override
    public TableType getType() {
        return TableType.DOCUMENT;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }
}
