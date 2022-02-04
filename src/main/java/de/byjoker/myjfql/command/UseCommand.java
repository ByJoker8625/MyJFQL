package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.server.session.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.ParsedLine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandHandler
public class UseCommand extends Command {

    public UseCommand() {
        super("use", Arrays.asList("COMMAND", "DATABASE"));
    }

    @Override
    public void execute(CommandSender sender, Map<String, List<String>> args) {
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

    @Nullable
    @Override
    public List<String> complete(@NotNull CommandSender sender, @NotNull ParsedLine line) {
        if (sender.getSession() == null) return null;

        final String args = line.line().toUpperCase();
        final String before = line.words().get(line.wordIndex() - 1).toUpperCase();

        if (!args.contains(" DATABASE")) {
            return Collections.singletonList("database");
        }

        if (before.equals("DATABASE")) {
            return MyJFQL.getInstance().getDatabaseService().getDatabases().stream().map(Database::getName)
                    .collect(Collectors.toList());
        }

        return null;
    }
}
