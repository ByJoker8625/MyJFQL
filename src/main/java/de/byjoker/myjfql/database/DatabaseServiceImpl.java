package de.byjoker.myjfql.database;

import de.byjoker.myjfql.util.FileFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class DatabaseServiceImpl implements DatabaseService {

    private final FileFactory fileFactory;
    private final List<Database> databases;

    public DatabaseServiceImpl(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
        this.databases = new ArrayList<>();
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
    public Database getDatabase(final String name) {
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

        for (final File file : Objects.requireNonNull(space.listFiles())) {
            final JSONObject jsonObject = fileFactory.load(file);
            final JSONArray jsonArray = jsonObject.getJSONArray("tables");

            final Database dataBase = new Database(jsonObject.getString("name"));

            for (int j = 0; j < jsonArray.length(); j++) {
                final JSONObject currentObject = jsonArray.getJSONObject(j);

                final Table table = new Table(currentObject.getString("name"), null, currentObject.getString("primary"));
                final List<String> list = new ArrayList<>();

                for (final Object obj : currentObject.getJSONArray("structure")) {
                    list.add(obj.toString());
                }

                table.setStructure(list);

                final JSONArray currentArray = currentObject.getJSONArray("columns");

                IntStream.range(0, currentArray.length()).mapToObj(currentArray::getJSONObject).forEach(curObject -> {
                    Column column = new Column();
                    column.setCreation(curObject.getLong("creation"));
                    column.setContent(curObject.getJSONObject("content").toMap());
                    table.addColumn(column);
                });

                dataBase.getTables().add(table);
            }

            databases.add(dataBase);
        }

    }

    @Override
    public void load(String identifier) {
        // TODO: 23.10.2021  
    }

    @Override
    public void unload(Database entity) {

    }

    @Override
    public void update(Database entity) {

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
            fileFactory.save(file, jsonObject);
        });
    }

    @Override
    public void collectGarbage() {

    }

}
