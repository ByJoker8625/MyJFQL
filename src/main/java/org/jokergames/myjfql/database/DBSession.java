package org.jokergames.myjfql.database;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.FileException;

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
        final DatabaseService dataBaseService = MyJFQL.getInstance().getDatabaseService();

        if (directories.containsKey(key)) {
            return directories.get(key);
        }

        if (dataBaseService.getDataBases().size() == 0)
            throw new FileException("No database exists!");
        else if (dataBaseService.getDataBase("test") != null)
            return "test";
        else
            return dataBaseService.getDataBases().get(0).getName();
    }

}
