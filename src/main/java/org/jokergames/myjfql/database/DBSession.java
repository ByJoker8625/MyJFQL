package org.jokergames.myjfql.database;

import org.jokergames.myjfql.command.ConsoleCommandSender;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.FileException;
import org.jokergames.myjfql.user.User;
import org.jokergames.myjfql.user.UserService;

import java.util.HashMap;
import java.util.Map;

public class DBSession {

    private final Map<String, String> databases;
    private final UserService userService;
    private final DatabaseService databaseService;
    private final ConsoleCommandSender consoleCommandSender;

    public DBSession(final UserService userService, final DatabaseService databaseService) {
        this.databases = new HashMap<>();
        this.userService = userService;
        this.databaseService = databaseService;
        this.consoleCommandSender = MyJFQL.getInstance().getConsoleCommandSender();
    }

    public void put(final String name, final String database) {
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

    public Database getDirectlyDatabase(final String name) {
        final String databaseName = get(name);

        if (databaseName == null) {
            return null;
        }

        return databaseService.getDataBase(databaseName);
    }

    public String get(final String name) {
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
                && databaseService.isCreated(name)) {
            put(name, name);
            return name;
        }


        if (databaseService.getDataBases().size() == 0) {
            throw new FileException("Can't load any database!");
        }

        return null;
    }

}
