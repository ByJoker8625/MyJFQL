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

        final User user = userService.getUser(name);

        if (databases.containsKey(name)
                && databases.get(name).equals(database))
            return;

        if (!user.hasPermission("use.database." + database)
                || user.hasPermission("-use.database." + database)) {
            return;
        }

        databases.put(name, database);
    }

    public Database getDirectlyDatabase(String name) {
        final String databaseName = get(name);

        if (databaseName == null) {
            return null;
        }

        return databaseService.getDatabase(databaseName);
    }

    public String get(String name) {
        if (name.equalsIgnoreCase(consoleCommandSender.getName())) {
            return databases.get(name);
        }

        final User user = userService.getUser(name);

        if (user == null) {
            return null;
        }

        if (user.hasPermission("-use.database.*")) {
            return null;
        }

        if (databases.containsKey(name)) {
            final String currentDatabase = databases.get(name);

            if (!user.hasPermission("use.database." + currentDatabase)
                    || user.hasPermission("-use.database." + currentDatabase)) {
                return null;
            }

            return currentDatabase;
        }

        if (user.isStaticDatabase()
                && databaseService.existsDatabase(name)) {
            put(name, name);
            return name;
        }


        if (databaseService.getDatabases().size() == 0) {
            throw new FileException("Can't load any database!");
        }

        return null;
    }

}
