package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.*;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandHandler
public class UserCommand extends ConsoleCommand {

    public UserCommand() {
        super("user", Arrays.asList("COMMAND", "CREATE", "PASSWORD", "GRANT", "REVOKE", "ACCESS", "DATABASE", "AT", "FROM", "DISPLAY", "LIST", "DELETE"));
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

            if (password.length() < 8) {
                sender.sendError("Password to short!");
                return;
            }

            if (userService.existsUserByName(name)) {
                sender.sendError("User already exists!");
                return;
            }

            final User user = new User(name, password);

            if (args.containsKey("DATABASE")) {
                String databaseName = (args.get("DATABASE").size() == 0) ? name : formatString(args.get("DATABASE"));

                if (databaseService.existsDatabaseByName(databaseName)) {
                    sender.sendError("Database already exists!");
                    return;
                }

                final Database database = new MapManagedDatabase(databaseName);
                databaseService.createDatabase(database);

                user.grantAccess(database.getId(), DatabaseAction.READ_WRITE);
                user.setPreferredDatabase(database.getId());
            }

            userService.createUser(user);
            sender.sendSuccess();
            return;
        }

        if (args.containsKey("DELETE")) {
            final String userIdentifier = formatString(args.get("DELETE"));

            if (userIdentifier == null) {
                sender.sendError("Undefined user!");
                return;
            }

            if (!userService.existsUserByIdentifier(userIdentifier)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            final String userId = userService.getUserByIdentifier(userIdentifier).getId();
            MyJFQL.getInstance().getSessionService().closeSessions(userId);

            userService.deleteUser(userId);
            sender.sendSuccess();
            return;
        }

        if (args.containsKey("GRANT") && args.containsKey("ACCESS") && args.containsKey("AT")) {
            final String userIdentifier = formatString(args.get("GRANT"));
            final String access = formatString(args.get("ACCESS"));
            final String databaseIdentifier = formatString(args.get("AT"));

            if (userIdentifier == null) {
                sender.sendError("Undefined user!");
                return;
            }

            if (access == null) {
                sender.sendError("Undefined access action!");
                return;
            }

            if (databaseIdentifier == null) {
                sender.sendError("Undefined database!");
                return;
            }

            if (!userService.existsUserByIdentifier(userIdentifier)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            if (!databaseService.existsDatabaseByIdentifier(databaseIdentifier)) {
                sender.sendError("Database doesn't exists!");
                return;
            }

            DatabaseAction action;

            try {
                action = DatabaseAction.valueOf(access.toUpperCase());
            } catch (Exception ex) {
                sender.sendError("Unknown access action!");
                return;
            }

            final User user = userService.getUserByIdentifier(userIdentifier);
            user.grantAccess(databaseService.getDatabaseByIdentifier(databaseIdentifier).getId(), action);
            userService.saveUser(user);

            sender.sendSuccess();
            return;
        }

        if (args.containsKey("REVOKE") && args.containsKey("ACCESS") && args.containsKey("FROM")) {
            final String userIdentifier = formatString(args.get("REVOKE"));
            final String databaseIdentifier = formatString(args.get("FROM"));

            if (userIdentifier == null) {
                sender.sendError("Undefined user!");
                return;
            }

            if (databaseIdentifier == null) {
                sender.sendError("Undefined database!");
                return;
            }

            if (!userService.existsUserByIdentifier(userIdentifier)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            if (!databaseService.existsDatabaseByIdentifier(databaseIdentifier)) {
                sender.sendError("Database doesn't exists!");
                return;
            }

            final User user = userService.getUserByName(userIdentifier);
            user.revokeAccess(databaseService.getDatabaseByIdentifier(databaseIdentifier).getId());
            userService.saveUser(user);

            sender.sendSuccess();
            return;
        }

        if (args.containsKey("DISPLAY")) {
            final String userIdentifier = formatString(args.get("DISPLAY"));

            if (userIdentifier == null) {
                sender.sendError("Undefined user!");
                return;
            }

            if (!userService.existsUserByIdentifier(userIdentifier)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            final User selectedUser = userService.getUserByIdentifier(userIdentifier);
            final Column column = new LegacyColumn();

            column.insert("id", selectedUser.getId());
            column.insert("name", selectedUser.getName());
            column.insert("accesses", selectedUser.getAccesses().toString());
            column.insert("preferred_database_id", String.valueOf(selectedUser.getPreferredDatabase()));

            sender.sendResult(Collections.singletonList(column), new String[]{"id", "name", "accesses", "preferred_database_id"});
            return;
        }

        if (args.containsKey("LIST")) {
            sender.sendResult(userService.getUsers().stream().map(User::getName).collect(Collectors.toList()), new String[]{"user_names"});
            return;
        }

        sender.sendSyntax();
    }
}
