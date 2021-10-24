package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandExecutor
public class UserCommand extends ConsoleCommand {

    public UserCommand() {
        super("user", Arrays.asList("COMMAND", "CREATE", "PASSWORD", "ADD", "PERMISSION", "REMOVE", "DATABASE", "UPDATE", "DISPLAY", "LIST", "DELETE"));
    }

    @Override
    public void handleConsoleCommand(ConsoleCommandSender sender, Map<String, List<String>> args) {
        final UserService userService = MyJFQL.getInstance().getUserService();
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();

        if (args.containsKey("CREATE") && args.containsKey("PASSWORD")) {
            final String name = formatString(args.get("CREATE"));
            final String password = formatString(args.get("PASSWORD"));

            if (name == null) {
                sender.sendError("Undefined name!");
                return;
            }

            if (name.contains("%") || name.contains("#") || name.contains("'")) {
                sender.sendError("Unauthorized characters in the name!");
                return;
            }

            if (password == null) {
                sender.sendError("Undefined password!");
                return;
            }

            if (userService.existsUser(name)) {
                sender.sendError("User already exists!");
                return;
            }

            final User user = new User(name, password);

            if (args.containsKey("DATABASE")) {
                if (databaseService.existsDatabase(name)) {
                    sender.sendError("Database already exists!");
                    return;
                }

                databaseService.saveDatabase(new Database(name));
                user.addPermission("use.table.*." + name);
                user.addPermission("use.database." + name);
                user.setStaticDatabase(true);
            }

            userService.createUser(user);
            sender.sendSuccess();
            return;
        }

        if (args.containsKey("DELETE")) {
            final String name = formatString(args.get("DELETE"));

            if (name == null) {
                sender.sendError("Undefined name!");
                return;
            }

            if (!userService.existsUser(name)) {
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
                sender.sendError("Undefined name!");
                return;
            }

            if (password == null) {
                sender.sendError("Undefined password!");
                return;
            }

            if (!userService.existsUser(name)) {
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
                sender.sendError("Undefined name!");
                return;
            }

            if (permission == null) {
                sender.sendError("Undefined permission!");
                return;
            }

            if (!userService.existsUser(name)) {
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
                sender.sendError("Undefined name!");
                return;
            }

            if (permission == null) {
                sender.sendError("Undefined permission!");
                return;
            }

            if (!userService.existsUser(name)) {
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
                sender.sendError("Undefined name!");
                return;
            }

            if (!userService.existsUser(name)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            final User selectedUser = userService.getUser(name);
            final Column column = new Column();
            column.putContent("Name", selectedUser.getName());
            column.putContent("Password", selectedUser.getPassword());
            column.putContent("Permissions", selectedUser.getPermissions().toString());

            sender.sendAnswer(Collections.singletonList(column), new String[]{"Name", "Password", "Permissions"});
            return;
        }

        if (args.containsKey("LIST")) {
            sender.sendAnswer(userService.getUsers().stream().map(User::getName).collect(Collectors.toList()), new String[]{"User"});
            return;
        }

        sender.sendSyntax();
    }
}
