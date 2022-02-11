package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.*;
import de.byjoker.myjfql.lang.TableEntryFilter;
import de.byjoker.myjfql.server.session.Session;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandHandler
public class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remove", Arrays.asList("COMMAND", "COLUMN", "FROM", "WHERE"));
    }

    @Override
    public void execute(CommandSender sender, @NotNull Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final Session session = sender.getSession();

        if (session == null) {
            sender.sendError("Session of this user is invalid!");
            return;
        }

        final Database database = session.getDatabase(databaseService);

        if (database == null) {
            sender.sendError("No database is in use for this user!");
            return;
        }

        if (args.containsKey("FROM")
                && args.containsKey("COLUMN")) {
            final String name = formatString(args.get("FROM"));
            final String entry = formatString(args.get("COLUMN"));

            if (name == null) {
                sender.sendError("Undefined table!");
                return;
            }

            if (entry == null) {
                sender.sendError("Undefined entry!");
                return;
            }

            if (!database.existsTable(name)) {
                sender.sendError("Table doesn't exists!");
                return;
            }

            if (!sender.allowed(database.getId(), DatabasePermissionLevel.READ_WRITE)) {
                sender.sendForbidden();
                return;
            }

            final Table table = database.getTable(name);

            if (!entry.equals("*")
                    && table.getEntry(entry) == null) {
                sender.sendError("Entry doesn't exist!");
                return;
            }

            if (args.containsKey("WHERE")) {
                List<TableEntry> entries;

                try {
                    entries = TableEntryFilter.filterByCommandLineArguments(table, args.get("WHERE"));
                } catch (Exception ex) {
                    sender.sendError(ex);
                    return;
                }

                if (entries == null) {
                    sender.sendError("Unknown statement error!");
                    return;
                }

                entries.stream().map(col -> col.selectStringify(table.getPrimary())).forEach(table::removeEntry);

            } else {
                if (!entry.equals("*")) {
                    table.removeEntry(entry);
                } else {
                    table.clear();
                }
            }

            sender.sendSuccess();
            database.saveTable(table);
            databaseService.saveDatabase(database);

            return;
        }

        sender.sendSyntax();
    }
}
