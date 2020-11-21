package de.jokergames.jfql.database;

import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.exception.FileException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Janick
 */

public class DBSession {

    private final Map<String, String> directories;

    public DBSession() {
        this.directories = new HashMap<>();
    }

    public void put(String key, String dir) {
        directories.put(key, dir);
    }

    public Set<String> keySet() {
        return directories.keySet();
    }

    public Collection<String> values() {
        return directories.values();
    }

    public String get(String key) {
        final DatabaseHandler dataBaseHandler = JFQL.getInstance().getDataBaseHandler();

        if (!directories.containsKey(key)) {
            if (dataBaseHandler.getDataBases().size() == 0)
                throw new FileException("No database exists!");
            else if (dataBaseHandler.getDataBase("test") != null)
                return "test";
            else
                return dataBaseHandler.getDataBases().get(0).getName();
        }

        return directories.get(key);
    }

}
