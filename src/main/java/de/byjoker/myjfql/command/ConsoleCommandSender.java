package de.byjoker.myjfql.command;

import de.byjoker.myjfql.console.Console;
import de.byjoker.myjfql.console.TablePrinter;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.exception.LanguageException;
import de.byjoker.myjfql.server.session.Session;

import java.util.Collection;

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
        if (!(structure instanceof String[]) && !(structure instanceof Collection))
            throw new LanguageException("Input is not an array!");

        if (!(obj instanceof Collection))
            throw new LanguageException("Input is not an collection!");

        String[] array;

        if (!(structure instanceof String[])) {
            Collection<String> strings = (Collection<String>) structure;
            array = new String[strings.size()];
            array = strings.toArray(array);
        } else {

            array = (String[]) structure;
        }

        final TablePrinter printer = new TablePrinter(array);

        try {
            final Collection<Column> columns = (Collection<Column>) obj;

            for (Column column : columns) {
                final String[] row = new String[array.length];

                for (int i = 0; i < array.length; i++) {
                    final Object value = column.select(array[i]);

                    if (value != null)
                        row[i] = value.toString();
                    else
                        row[i] = "null";
                }

                printer.addRow(row);
            }

        } catch (Exception ex) {
            final Collection<String> strings = (Collection<String>) obj;
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
