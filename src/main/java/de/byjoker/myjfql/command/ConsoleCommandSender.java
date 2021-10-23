package de.byjoker.myjfql.command;

import de.byjoker.myjfql.console.Console;
import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.exception.CommandException;
import de.byjoker.myjfql.util.TablePrinter;

import java.util.List;

public class ConsoleCommandSender extends CommandSender {

    private final Console console;

    public ConsoleCommandSender(final Console console) {
        super("Console", "127.0.0.1");
        this.console = console;
    }

    @Override
    public boolean hasPermission(String permission) {
        return !permission.startsWith("-");
    }

    @Override
    public boolean isStaticDatabase() {
        return false;
    }

    @Override
    public void sendError(Object obj) {
        console.logError(obj.toString());
    }

    @Override
    public void sendInfo(Object obj) {
        console.logInfo(obj.toString());
    }

    @Override
    public void sendForbidden() {
        console.logError("You don't have the permissions to do that!");
    }

    @Override
    public void sendSyntax() {
        console.logError("Unknown syntax!");
    }

    @Override
    public void sendSuccess() {
        console.logInfo("Command successfully executed.");
    }


    @Override
    public void sendAnswer(Object obj, Object structure) {
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
        console.println(obj.toString());
    }

    public Console getConsole() {
        return console;
    }
}
