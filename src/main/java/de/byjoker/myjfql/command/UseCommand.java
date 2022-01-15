package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.server.session.Session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandHandler
public class UseCommand extends Command {

    public UseCommand() {
        super("use", Arrays.asList("COMMAND", "DATABASE"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final Session session = sender.getSession();

        if (session == null) {
            sender.sendError("Session of this user is invalid!");
            return;
        }

        if (args.containsKey("DATABASE")) {
            final String databaseIdentifier = formatString(args.get("DATABASE"));

            if (databaseIdentifier == null) {
                sender.sendError("Undefined database!");
                return;
            }

            if (!databaseService.existsDatabaseByIdentifier(databaseIdentifier)) {
                sender.sendError("Database doesn't exists!");
                return;
            }

            final Database database = databaseService.getDatabaseByIdentifier(databaseIdentifier);

            if (!sender.allowed(database.getId(), DatabaseAction.READ)) {
                sender.sendForbidden();
                return;
            }

            session.setDatabaseId(database.getId());
            MyJFQL.getInstance().getSessionService().saveSession(session);

            sender.sendSuccess();
            return;
        }

        sender.sendSyntax();
    }
}
