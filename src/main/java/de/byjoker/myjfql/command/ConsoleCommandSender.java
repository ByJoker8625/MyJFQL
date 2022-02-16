package de.byjoker.myjfql.command;

import de.byjoker.myjfql.console.Console;
import de.byjoker.myjfql.console.TablePrinter;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.DatabasePermissionLevel;
import de.byjoker.myjfql.database.TableEntry;
import de.byjoker.myjfql.network.session.Session;
import de.byjoker.myjfql.util.ResultType;

import java.util.Collection;

public class ConsoleCommandSender extends CommandSender {


    public ConsoleCommandSender() {
        super("%CONSOLE%", null);
    }

    @Override
    public boolean allowed(String database, DatabasePermissionLevel action) {
        return true;
    }

    @Override
    public void sendError(Object obj) {
        getConsole().logError(obj.toString());
    }

    @Override
    public void sendForbidden() {
        getConsole().logError("You don't have the permissions to do that!");
    }

    @Override
    public void sendSyntax() {
        getConsole().logError("Unknown syntax!");
    }

    @Override
    public void sendSuccess() {
        getConsole().logInfo("Command successfully executed.");
    }

    @Override
    public void sendResult(Collection<TableEntry> entries, Collection<String> structure, ResultType resultType) {
        final String[] fields = structure.toArray(new String[0]);
        final TablePrinter printer = new TablePrinter(fields);

        for (TableEntry tableEntry : entries) {
            final String[] row = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                final Object value = tableEntry.select(fields[i]);

                if (value != null)
                    row[i] = value.toString();
                else
                    row[i] = "null";
            }

            printer.addRow(row);
        }

        printer.print();
    }

    @Override
    public void send(Object obj) {
        getConsole().println(obj.toString());
    }

    @Override
    public Session getSession() {
        return MyJFQL.getInstance().getSessionService().getSession(super.getName());
    }

    public Console getConsole() {
        return MyJFQL.getInstance().getConsole();
    }
}
