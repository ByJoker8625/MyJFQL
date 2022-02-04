package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.server.session.Session;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandHandler
public class DeleteCommand extends Command {

    public DeleteCommand() {
        super("delete", Arrays.asList("COMMAND", "DATABASE", "TABLE"));
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final Session session = sender.getSession();

        if (session == null) {
            sender.sendError("Session of this user is invalid!");
            return;
        }

        if (args.containsKey("TABLE")) {
            final String name = formatString(args.get("TABLE"));
            final Database database = session.getDatabase(MyJFQL.getInstance().getDatabaseService());

            if (database == null) {
                sender.sendError("No database is in use for this user!");
                return;
            }

            if (!sender.allowed(database.getId(), DatabaseAction.READ_WRITE)) {
                sender.sendForbidden();
                return;
            }

            if (!database.existsTable(name)) {
                sender.sendError("Table doesn't exist!");
                return;
            }

            sender.sendSuccess();

            database.deleteTable(name);
            databaseService.saveDatabase(database);
            return;
        }

        if (args.containsKey("DATABASE")) {
            if (sender instanceof RestCommandSender) {
                sender.sendForbidden();
                return;
            }

            final String identifier = formatString(args.get("DATABASE"));

            if (identifier == null) {
                sender.sendError("Undefined database!");
                return;
            }

            if (!databaseService.existsDatabaseByIdentifier(identifier)) {
                sender.sendError("Database doesn't exist!");
                return;
            }

            databaseService.deleteDatabase(databaseService.getDatabaseByIdentifier(identifier).getId());
            sender.sendSuccess();
            return;
        }

        sender.sendSyntax();
    }

}
