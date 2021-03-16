package org.jokergames.myjfql.database;

import org.jokergames.myjfql.util.FileFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author Janick
 */

public class DatabaseService {

    private final FileFactory fileFactory;
    private final List<Database> databases;

    public DatabaseService(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
        this.databases = new ArrayList<>();
    }

    public void saveDataBase(Database database) {
        for (int i = 0, databasesSize = databases.size(); i < databasesSize; i++) {
            if (databases.get(i).getName().equals(database.getName())) {
                databases.set(i, database);
                return;
            }
        }

        databases.add(database);
    }

    public void deleteDatabase(String name) {
        deleteDatabase(new Database(name));
    }


    public void deleteDatabase(Database database) {
        databases.removeIf(database1 -> database1.getName().equals(database.getName()));
    }

    public Database getDataBase(String name) {
        return databases.stream().filter(database -> database.getName().equals(name)).findFirst().orElse(null);
    }

    public List<Database> getDataBases() {
        return databases;
    }

    public void init() {
        for (File file : Objects.requireNonNull(new File("database").listFiles())) {
            final JSONObject jsonObject = fileFactory.load(file);
            final JSONArray jsonArray = jsonObject.getJSONArray("tables");

            Database dataBase = new Database(jsonObject.getString("name"));

            for (int j = 0; j < jsonArray.length(); j++) {
                final JSONObject currentObject = jsonArray.getJSONObject(j);

                Table table = new Table(currentObject.getString("name"), null, currentObject.getString("primary"));

                List<String> list = new ArrayList<>();

                for (Object obj : currentObject.getJSONArray("structure")) {
                    list.add(obj.toString());
                }

                table.setStructure(list);

                JSONArray currentArray = currentObject.getJSONArray("columns");

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

    public void update() {
        for (File file : Objects.requireNonNull(new File("database").listFiles())) {
            file.delete();
        }

        databases.forEach(database -> {
            final File file = new File("database/" + database.getName() + ".json");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", database.getName());
            jsonObject.put("tables", database.getTables());
            fileFactory.save(file, jsonObject);
        });
    }

}
