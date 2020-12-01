package de.jokergames.jfql.database;

import de.jokergames.jfql.util.FileFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class DatabaseService {

    private FileFactory fileFactory;

    public DatabaseService(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
    }

    public void saveDataBase(Database dataBase) {
        final File file = new File("database/" + dataBase.getName() + ".json");

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", dataBase.getName());
        jsonObject.put("tables", dataBase.getTables());

        fileFactory.save(file, jsonObject);
    }

    public Database getDataBase(String name) {
        final File file = new File("database/" + name + ".json");

        if (!file.exists()) {
            return null;
        }

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


        return dataBase;
    }

    public List<Database> getDataBases() {
        List<Database> databases = new ArrayList<>();

        for (File file : new File("database").listFiles()) {
            Database current = getDataBase(file.getName().replace(".json", ""));

            if (current != null)
                databases.add(current);
        }

        return databases;
    }

}
