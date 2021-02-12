package org.jokergames.myjfql.database;

import org.jokergames.myjfql.util.FileFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public Database getDataBase(String name) {
        return databases.stream().filter(database -> database.getName().equals(name)).findFirst().orElse(null);
    }

    public List<Database> getDataBases() {
        return databases;
    }

    public void init() {
        for (File file : new File("database").listFiles()) {
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

                for (int i = 0; i < currentArray.length(); i++) {
                    final JSONObject curObject = currentArray.getJSONObject(i);

                    Column column = new Column();
                    column.setCreation(curObject.getLong("creation"));
                    column.setContent(curObject.getJSONObject("content").toMap());

                    table.addColumn(column);
                }

                dataBase.getTables().add(table);
            }

            databases.add(dataBase);
        }

    }

    public void update() {
        for (Database database : databases) {
            final File file = new File("database/" + database.getName() + ".json");

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", database.getName());
            jsonObject.put("tables", database.getTables());

            fileFactory.save(file, jsonObject);
        }
    }

}
