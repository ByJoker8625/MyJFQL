package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.user.User;
import org.jokergames.myjfql.user.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UserCommand extends ConsoleCommand {

    public UserCommand() {
        super("user", Arrays.asList("COMMAND", "CREATE", "PASSWORD", "ADD", "PERMISSION", "REMOVE", "DATABASE", "UPDATE", "DISPLAY", "DELETE"));
    }

    @Override
    public void handleConsoleCommand(final ConsoleCommandSender sender, final Map<String, List<String>> args) {
        final UserService userService = MyJFQL.getInstance().getUserService();
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();


        if (args.containsKey("CREATE") && args.containsKey("PASSWORD")) {
            final String name = formatString(args.get("CREATE"));
            final String password = formatString(args.get("PASSWORD"));

            if (name == null) {
                sender.sendError("Unknown name!");
                return;
            }

            if (password == null) {
                sender.sendError("Unknown password!");
                return;
            }

            if (userService.isCreated(name)) {
                sender.sendError("User already exists!");
                return;
            }

            final User user = new User(name, password);

            if (args.containsKey("DATABASE")) {
                if (databaseService.isCreated(name)) {
                    sender.sendError("Database already exists!");
                    return;
                }

                databaseService.saveDataBase(new Database(name));
                user.addPermission("use.table.*." + name);
                user.addPermission("use.database." + name);
                user.setStaticDatabase(true);
            }

            userService.saveUser(user);
            sender.sendSuccess();
            return;
        }

        if (args.containsKey("DELETE")) {
            final String name = formatString(args.get("DELETE"));

            if (name == null) {
                sender.sendError("Unknown name!");
                return;
            }

            if (!userService.isCreated(name)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            userService.deleteUser(name);
            sender.sendSuccess();
            return;
        }

        if (args.containsKey("UPDATE") && args.containsKey("PASSWORD")) {
            final String name = formatString(args.get("UPDATE"));
            final String password = formatString(args.get("PASSWORD"));

            if (name == null) {
                sender.sendError("Unknown name!");
                return;
            }

            if (password == null) {
                sender.sendError("Unknown password!");
                return;
            }

            if (!userService.isCreated(name)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            final User user = userService.getUser(name);
            user.setPassword(password);
            userService.saveUser(user);

            sender.sendSuccess();
            return;
        }

        if (args.containsKey("ADD") && args.containsKey("PERMISSION")) {
            final String name = formatString(args.get("ADD"));
            final String permission = formatString(args.get("PERMISSION"));

            if (name == null) {
                sender.sendError("Unknown name!");
                return;
            }

            if (permission == null) {
                sender.sendError("Unknown permission!");
                return;
            }

            if (!userService.isCreated(name)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            final User user = userService.getUser(name);
            user.addPermission(permission);
            userService.saveUser(user);

            sender.sendSuccess();
            return;
        }

        if (args.containsKey("REMOVE") && args.containsKey("PERMISSION")) {
            final String name = formatString(args.get("REMOVE"));
            final String permission = formatString(args.get("PERMISSION"));

            if (name == null) {
                sender.sendError("Unknown name!");
                return;
            }

            if (permission == null) {
                sender.sendError("Unknown permission!");
                return;
            }

            if (!userService.isCreated(name)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            final User user = userService.getUser(name);
            user.removePermission(permission);
            userService.saveUser(user);

            sender.sendSuccess();
            return;
        }

        if (args.containsKey("DISPLAY")) {
            final String name = formatString(args.get("DISPLAY"));

            if (name == null) {
                sender.sendError("Unknown name!");
                return;
            }

            if (!userService.isCreated(name)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            sender.send(userService.getUser(name));
            return;
        }

        sender.sendSyntax();
    }
}
