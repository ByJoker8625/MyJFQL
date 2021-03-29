package org.jokergames.myjfql.database;

import org.jokergames.myjfql.exception.CommandException;
import org.jokergames.myjfql.exception.FileException;
import org.jokergames.myjfql.user.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Janick
 */

public class DBSession {

    private final Map<String, String> directories;
    private final UserService userService;
    private final DatabaseService databaseService;

    public DBSession(final UserService userService, final DatabaseService databaseService) {
        this.directories = new HashMap<>();
        this.userService = userService;
        this.databaseService = databaseService;
    }

    public void put(final String key, final String dir) {
        if (directories.containsKey(key)
                && directories.get(key).equals(dir))
            return;

        directories.put(key, dir);
    }

    public String get(final String key) {
        if (userService.isCreated(key)
                && userService.getUser(key).isStaticDatabase()
                && databaseService.isCreated(key)) {
            return key;
        }

        if (directories.containsKey(key)) {
            return directories.get(key);
        }

        if (databaseService.getDataBases().size() == 0) {
            throw new FileException("Can't load any database!");
        }

        return databaseService.getDataBases().get(0).getName();
    }

}
