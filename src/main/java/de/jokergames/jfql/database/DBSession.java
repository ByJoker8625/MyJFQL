package de.jokergames.jfql.database;

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
        if (!directories.containsKey(key)) {
            return "test";
        }

        return directories.get(key);
    }

}
