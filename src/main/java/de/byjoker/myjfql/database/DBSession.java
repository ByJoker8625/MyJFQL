package de.byjoker.myjfql.database;

import de.byjoker.myjfql.command.ConsoleCommandSender;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.exception.FileException;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.UserService;

import java.util.HashMap;
import java.util.Map;

public class DBSession {

    private final Map<String, String> databases;
    private final UserService userService;
    private final DatabaseService databaseService;
    private final ConsoleCommandSender consoleCommandSender;

    public DBSession(UserService userService, DatabaseService databaseService) {
        this.databases = new HashMap<>();
        this.userService = userService;
        this.databaseService = databaseService;
        this.consoleCommandSender = MyJFQL.getInstance().getConsoleCommandSender();
    }

    public void put(String name, String database) {
        if (name.equalsIgnoreCase(consoleCommandSender.getName())) {
            databases.put(name, database);
            return;
        }

        final User user = userService.getUserByName(name);

        if (databases.containsKey(name)
                && databases.get(name).equals(database))
            return;

        databases.put(name, database);
    }

    public Database getDirectlyDatabase(String name) {
        final String databaseName = get(name);

        if (databaseName == null) {
            return null;
        }

        return databaseService.getDatabaseByName(databaseName);
    }

    public String get(String name) {
        if (name.equalsIgnoreCase(consoleCommandSender.getName())) {
            return databases.get(name);
        }

        final User user = userService.getUserByName(name);

        if (user == null) {
            return null;
        }

        if (databaseService.getDatabases().size() == 0) {
            throw new FileException("Can't load any database!");
        }

        return null;
    }

}
