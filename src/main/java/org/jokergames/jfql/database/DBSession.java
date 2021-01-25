package org.jokergames.jfql.database;

import org.jokergames.jfql.core.JFQL;
import org.jokergames.jfql.exception.FileException;

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
        final DatabaseService dataBaseService = JFQL.getInstance().getDatabaseService();

        if (!directories.containsKey(key)) {
            if (dataBaseService.getDataBases().size() == 0)
                throw new FileException("No database exists!");
            else if (dataBaseService.getDataBase("test") != null)
                return "test";
            else
                return dataBaseService.getDataBases().get(0).getName();
        }

        return directories.get(key);
    }

}
