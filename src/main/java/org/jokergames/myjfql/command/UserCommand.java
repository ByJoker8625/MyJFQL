package org.jokergames.myjfql.command;

import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.user.RemoteUser;
import org.jokergames.myjfql.user.User;
import org.jokergames.myjfql.user.UserService;

import java.util.List;
import java.util.Map;

/**
 * @author Janick
 * @language This commands is not a JFQL query. It is only for the DBMS management.
 */


public class UserCommand extends Command {

    public UserCommand() {
        super("USER", List.of("COMMAND", "CREATE", "DATABASE", "PASSWORD", "DELETE", "ADD", "REMOVE", "DISPLAY", "PERMISSION"), List.of("USR"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        if (executor instanceof RemoteExecutor) {
            return false;
        }

        final UserService userService = MyJFQL.getInstance().getUserService();
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();

        if (arguments.containsKey("CREATE") && arguments.containsKey("PASSWORD")) {
            String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("CREATE"));
            String password = MyJFQL.getInstance().getFormatter().formatString(arguments.get("PASSWORD"));

            if (userService.getUser(name) != null) {
                MyJFQL.getInstance().getConsole().logError("User '" + name + "' already exists!");
                return true;
            }

            final User usr = new RemoteUser(name, password);

            if (arguments.containsKey("DATABASE")) {
                if (databaseService.getDataBase(usr.getName()) != null) {
                    MyJFQL.getInstance().getConsole().logError("A database with name '" + user.getName() + "' already exists!");
                    return true;
                }

                final Database database = new Database(usr.getName());

                {
                    usr.getPermissions().add("execute.create");
                    usr.getPermissions().add("execute.delete");
                    usr.getPermissions().add("execute.remove");
                    usr.getPermissions().add("execute.insert");
                    usr.getPermissions().add("execute.select");
                    usr.getPermissions().add("execute.list");
                    usr.getPermissions().add("execute.use");

                    usr.getPermissions().add("execute.create.table.*");
                    usr.getPermissions().add("execute.delete.database." + database.getName());
                    usr.getPermissions().add("execute.delete.table.*");
                    usr.getPermissions().add("execute.remove.database." + database.getName() + ".*");
                    usr.getPermissions().add("execute.insert.database." + database.getName() + ".*");
                    usr.getPermissions().add("execute.select.database." + database.getName() + ".*");
                    usr.getPermissions().add("execute.list.tables");
                    usr.getPermissions().add("execute.list.databases");
                    usr.getPermissions().add("execute.use." + database.getName());
                }

                databaseService.saveDataBase(database);
            }

            userService.saveUser(usr);
            MyJFQL.getInstance().getConsole().logInfo("User '" + name + "' was created.");
            return true;
        }

        if (arguments.containsKey("DELETE")) {
            String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("DELETE"));

            if (userService.getUser(name) == null) {
                MyJFQL.getInstance().getConsole().logError("User '" + name + "' doesn't exists!");
                return true;
            }

            final User usr = userService.getUser(name);

            if (usr.is(User.Property.NO_DELETE)) {
                MyJFQL.getInstance().getConsole().logError("Can't delete user '" + usr.getName() + "'!");
                return true;
            }

            MyJFQL.getInstance().getConsole().logInfo("User '" + name + "' was deleted.");
            usr.getFile().delete();
            return true;
        }

        if (arguments.containsKey("DISPLAY")) {
            String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("DISPLAY"));

            if (userService.getUser(name) == null) {
                MyJFQL.getInstance().getConsole().logError("User '" + name + "' doesn't exists!");
                return true;
            }

            final User usr = userService.getUser(name);
            MyJFQL.getInstance().getConsole().log(usr.toString());
            return true;
        }

        if (arguments.containsKey("ADD") && arguments.containsKey("PERMISSION")) {
            String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("ADD"));
            String permission = MyJFQL.getInstance().getFormatter().formatString(arguments.get("PERMISSION")).toLowerCase();

            if (userService.getUser(name) == null) {
                MyJFQL.getInstance().getConsole().logError("User '" + name + "' doesn't exists!");
                return true;
            }

            final User usr = userService.getUser(name);

            if (usr.is(User.Property.NO_EDIT)) {
                MyJFQL.getInstance().getConsole().logError("Can't edit user '" + usr.getName() + "'!");
                return true;
            }

            MyJFQL.getInstance().getConsole().logInfo("Add permission '" + permission + "' to user '" + usr.getName() + "'.");
            usr.getPermissions().add(permission);
            userService.saveUser(usr);
            return true;
        }

        if (arguments.containsKey("REMOVE") && arguments.containsKey("PERMISSION")) {
            String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("REMOVE"));
            String permission = MyJFQL.getInstance().getFormatter().formatString(arguments.get("PERMISSION")).toLowerCase();

            if (userService.getUser(name) == null) {
                MyJFQL.getInstance().getConsole().logError("User '" + name + "' doesn't exists!");
                return true;
            }

            final User usr = userService.getUser(name);

            if (usr.is(User.Property.NO_EDIT)) {
                MyJFQL.getInstance().getConsole().logError("Can't edit user '" + usr.getName() + "'!");
                return true;
            }

            MyJFQL.getInstance().getConsole().logInfo("Remove permission '" + permission + "' to user '" + usr.getName() + "'.");
            usr.getPermissions().remove(permission);
            userService.saveUser(usr);
            return true;
        }

        MyJFQL.getInstance().getConsole().logError("Unknown syntax!");
        return true;
    }
}
