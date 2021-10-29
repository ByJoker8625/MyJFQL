package de.byjoker.myjfql.command;

import de.byjoker.myjfql.console.Console;
import de.byjoker.myjfql.console.TablePrinter;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.exception.CommandException;
import de.byjoker.myjfql.user.session.Session;

import java.util.List;

public class ConsoleCommandSender extends CommandSender {


    public ConsoleCommandSender() {
        super("%CONSOLE%", null);
    }

    @Override
    public boolean allowed(String database, DatabaseAction action) {
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
    public void sendResult(Object obj, Object structure) {
        if (!(structure instanceof String[]) && !(structure instanceof List))
            throw new CommandException("Input is not an array!");

        if (!(obj instanceof List))
            throw new CommandException("Input is not an list!");

        String[] array;

        if (!(structure instanceof String[])) {
            List<String> strings = (List<String>) structure;
            array = new String[strings.size()];
            array = strings.toArray(array);
        } else {

            array = (String[]) structure;
        }

        final TablePrinter printer = new TablePrinter(array);

        try {
            final List<Column> columns = (List<Column>) obj;

            for (final Column column : columns) {
                final String[] row = new String[array.length];

                for (int i = 0; i < array.length; i++) {
                    final Object value = column.getContent(array[i]);

                    if (value != null)
                        row[i] = value.toString();
                    else
                        row[i] = "null";
                }

                printer.addRow(row);
            }

        } catch (Exception ex) {
            final List<String> strings = (List<String>) obj;
            strings.forEach(printer::addRow);
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
