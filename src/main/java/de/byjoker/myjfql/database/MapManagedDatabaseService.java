package de.byjoker.myjfql.database;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.exception.FileException;
import de.byjoker.myjfql.util.FileFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.stream.IntStream;

public class MapManagedDatabaseService implements DatabaseService {

    private final FileFactory factory;
    private final Map<String, Database> databases;

    public MapManagedDatabaseService() {
        this.factory = new FileFactory();
        this.databases = new HashMap<>();
    }

    @Override
    public void createDatabase(Database database) {
        if (getDatabaseByName(database.getName()) != null)
            throw new FileException("Database already exists!");

        if (existsDatabase(database.getId())) {
            database.regenerateId();
            createDatabase(database);
            return;
        }

        saveDatabase(database);
    }

    @Override
    public void saveDatabase(Database database) {
        databases.put(database.getId(), database);
    }

    @Override
    public boolean existsDatabaseByIdentifier(String identifier) {
        if (identifier.startsWith("#"))
            return existsDatabase(identifier.replaceFirst("#", ""));

        return existsDatabaseByName(identifier);
    }

    @Override
    public boolean existsDatabaseByName(String name) {
        return databases.values().stream().anyMatch(database -> database.getName().equals(name));
    }

    @Override
    public boolean existsDatabase(String id) {
        return databases.containsKey(id);
    }

    @Override
    public void deleteDatabase(String id) {
        new File("database/" + id + ".json").delete();
        databases.remove(id);
    }

    @Override
    public Database getDatabaseByIdentifier(String identifier) {
        if (identifier.startsWith("#")) {
            return getDatabase(identifier.replaceFirst("#", ""));
        }

        return getDatabaseByName(identifier);
    }

    @Override
    public Database getDatabaseByName(String name) {
        return databases.values().stream().filter(database -> database.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public Database getDatabase(String id) {
        return databases.get(id);
    }

    @Override
    public Collection<Database> getDatabases() {
        return databases.values();
    }

    @Override
    public void loadAll() {
        loadAll(new File("database"));
    }

    @Override
    public void loadAll(File space) {
        databases.clear();

        for (File file : Objects.requireNonNull(space.listFiles())) {
            final JSONObject jsonDatabase = factory.load(file);
            final JSONArray jsonTables = jsonDatabase.getJSONArray("tables");

            final String name = file.getName().replaceFirst(".json", "");

            if (name.contains("%") || name.contains("#") || name.contains("'")) {
                MyJFQL.getInstance().getConsole().logWarning("Database used unauthorized characters in the id!");
                continue;
            }

            final MapManagedDatabase database = new MapManagedDatabase(name, jsonDatabase.getString("name"));

            for (int j = 0; j < jsonTables.length(); j++) {
                final JSONObject jsonTable = jsonTables.getJSONObject(j);

                final MapManagedTable table = new MapManagedTable(jsonTable.getString("name"), null, jsonTable.getString("primary"));
                final List<String> list = new ArrayList<>();

                for (final Object obj : jsonTable.getJSONArray("structure")) {
                    list.add(obj.toString());
                }

                table.setStructure(list);

                final JSONArray jsonColumns = jsonTable.getJSONArray("columns");

                IntStream.range(0, jsonColumns.length()).mapToObj(jsonColumns::getJSONObject).forEach(jsonColumn -> {
                    Column column = new CompiledColumn();
                    column.setCreatedAt(jsonColumn.getLong("creation"));
                    column.setContent(jsonColumn.getJSONObject("content").toMap());
                    table.addColumn(column);
                });

                if (!table.getName().contains("%") && !table.getName().contains("#") && !table.getName().contains("'"))
                    database.saveTable(table);
                else
                    MyJFQL.getInstance().getConsole().logWarning("Table used unauthorized characters in the name!");
            }

            if (!database.getName().contains("%") && !database.getName().contains("#") && !database.getName().contains("'"))
                databases.put(database.getId(), database);
            else
                MyJFQL.getInstance().getConsole().logWarning("Database used unauthorized characters in the name!");
        }
    }

    @Override
    public void updateAll() {
        updateAll(new File("database"));
    }

    @Override
    public void updateAll(File space) {
        databases.values().forEach(database -> {
            final File file = new File(space.getPath() + "/" + database.getId() + ".json");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", database.getName());
            jsonObject.put("tables", database.getTables());
            factory.save(file, jsonObject);
        });
    }

}
