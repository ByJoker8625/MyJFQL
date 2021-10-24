package de.byjoker.myjfql.database;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.exception.FileException;
import de.byjoker.myjfql.util.FileFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class DatabaseServiceImpl implements DatabaseService {

    private final FileFactory factory;
    private final List<Database> databases;

    public DatabaseServiceImpl() {
        this.factory = new FileFactory();
        this.databases = new ArrayList<>();
    }

    @Override
    public void createDatabase(Database database) {
        if (getDatabase(database.getName()) != null)
            throw new FileException("File '" + database.getName() + ".json' already exists!");

        saveDatabase(database);
    }

    @Override
    public void saveDatabase(Database database) {
        for (int i = 0, databasesSize = databases.size(); i < databasesSize; i++) {
            if (databases.get(i).getName().equals(database.getName())) {
                databases.set(i, database);
                return;
            }
        }

        databases.add(database);
    }

    @Override
    public boolean existsDatabase(String name) {
        return databases.stream().anyMatch(database -> database.getName().equals(name));
    }

    @Override
    public void deleteDatabase(String name) {
        databases.removeIf(database -> database.getName().equals(name));
        new File("database/" + name + ".json").delete();
    }

    @Override
    public Database getDatabase(String name) {
        return databases.stream().filter(database -> database.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public List<Database> getDatabases() {
        return databases;
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

            final Database database = new Database(jsonDatabase.getString("name"));

            for (int j = 0; j < jsonTables.length(); j++) {
                final JSONObject jsonTable = jsonTables.getJSONObject(j);

                final Table table = new Table(jsonTable.getString("name"), null, jsonTable.getString("primary"));
                final List<String> list = new ArrayList<>();

                for (final Object obj : jsonTable.getJSONArray("structure")) {
                    list.add(obj.toString());
                }

                table.setStructure(list);

                final JSONArray jsonColumns = jsonTable.getJSONArray("columns");

                IntStream.range(0, jsonColumns.length()).mapToObj(jsonColumns::getJSONObject).forEach(jsonColumn -> {
                    Column column = new Column();
                    column.setCreation(jsonColumn.getLong("creation"));
                    column.setContent(jsonColumn.getJSONObject("content").toMap());
                    table.addColumn(column);
                });

                if (!table.getName().contains("%") && !table.getName().contains("#") && !table.getName().contains("'"))
                    database.saveTable(table);
                else
                    MyJFQL.getInstance().getConsole().logWarning("Table '" + table.getName() + "' in '" + database.getName() + "' used unauthorized characters in the name!");
            }

            if (!database.getName().contains("%") && !database.getName().contains("#") && !database.getName().contains("'"))
                databases.add(database);
            else
                MyJFQL.getInstance().getConsole().logWarning("Database '" + database.getName() + "' used unauthorized characters in the name!");
        }

    }

    @Override
    public void updateAll() {
        updateAll(new File("database"));
    }

    @Override
    public void updateAll(File space) {
        databases.forEach(database -> {
            final File file = new File(space.getPath() + "/" + database.getName() + ".json");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", database.getName());
            jsonObject.put("tables", database.getTables());
            factory.save(file, jsonObject);
        });
    }

}
