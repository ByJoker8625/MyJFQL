package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.database.Column;
import de.jokergames.jfql.database.Database;
import de.jokergames.jfql.database.DatabaseHandler;
import de.jokergames.jfql.database.Table;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.user.User;
import de.jokergames.jfql.util.TablePrinter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class SelectCommand extends Command {


    public SelectCommand() {
        super("SELECT", List.of("COMMAND", "WHERE", "FROM", "VALUE", "PRIMARY-KEY", "LIMIT"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseHandler dataBaseHandler = JFQL.getInstance().getDataBaseHandler();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.select")) {
                return false;
            }

            if (arguments.containsKey("VALUE") && arguments.containsKey("FROM")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));
                int limit = -1;

                if (arguments.containsKey("LIMIT")) {
                    limit = JFQL.getInstance().getFormatter().formatInteger(arguments.get("LIMIT"));

                    if (limit <= -1) {
                        remote.send(JFQL.getInstance().getBuilder().buildBadMethod(new CommandException("Limit can't be smaller than 0!")));
                        return true;
                    }
                }

                final Database dataBase = dataBaseHandler.getDataBase(JFQL.getInstance().getDBSession().get(user.getName()));

                if (dataBase.getTable(name) == null) {
                    remote.send(JFQL.getInstance().getBuilder().buildBadMethod(new CommandException("Database doesn't exists!")));
                    return true;
                }

                final Table table = dataBase.getTable(name);

                if (!user.hasPermission("execute.select.database." + dataBase.getName() + ".*") && !user.hasPermission("execute.select.database." + dataBase.getName() + "." + table.getName())) {
                    return false;
                }

                List<String> values = new ArrayList<>();

                for (String var : arguments.get("VALUE")) {
                    if (var.equals("*")) {
                        values = table.getStructure();
                        break;
                    }

                    values.add(var);
                }

                if (values.isEmpty()) {
                    remote.send(JFQL.getInstance().getBuilder().buildBadMethod(new CommandException("No values to select!")));
                    return true;
                }

                for (String var : values) {
                    if (!table.getStructure().contains(var)) {
                        remote.send(JFQL.getInstance().getBuilder().buildBadMethod(new CommandException("Unknown key!")));
                        return true;
                    }
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    Column column = table.getColumn(JFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));

                    if (column == null) {
                        remote.send(JFQL.getInstance().getBuilder().buildBadMethod(new FileNotFoundException()));
                        return true;
                    }

                    List<String> list;

                    if (!values.contains(table.getPrimary())) {
                        list = new ArrayList<>();
                        list.add(table.getPrimary());
                        list.addAll(values);
                    } else {
                        list = new ArrayList<>(values);
                    }

                    String[] structure = new String[list.size()];
                    int index = 0;

                    for (String var : list) {
                        structure[index] = var;
                        index++;
                    }

                    List<Column> columns = new ArrayList<>();

                    if (limit != 0) {
                        columns.add(column);
                    }

                    remote.send(JFQL.getInstance().getBuilder().buildAnswer(columns, table.getStructure()));
                } else if (arguments.containsKey("WHERE")) {
                    List<String> list;

                    if (!values.contains(table.getPrimary())) {
                        list = new ArrayList<>();
                        list.add(table.getPrimary());
                        list.addAll(values);
                    } else {
                        list = new ArrayList<>(values);
                    }

                    String[] structure = new String[list.size()];
                    int index = 0;

                    for (String var : list) {
                        structure[index] = var;
                        index++;
                    }

                    List<Column> columns = new ArrayList<>();

                    String[] where = JFQL.getInstance().getFormatter().formatString(arguments.get("WHERE")).replace(" or ", " OR ").split(" OR ");
                    List<List<String[]>> conditions = new ArrayList<>();

                    for (int j = 0; j < where.length; j++) {
                        String[] args = where[j].replace(" and ", " AND ").split(" AND ");

                        List<String[]> list1 = new ArrayList<>();

                        for (int i = 0; i < args.length; i++) {
                            String[] strings = args[i].split(" = ");

                            strings[0] = strings[0].replace("'", "");
                            strings[1] = strings[1].replace("'", "");

                            if (!table.getStructure().contains(strings[0])) {
                                remote.send(JFQL.getInstance().getBuilder().buildBadMethod(new CommandException("Unknown key!")));
                                return true;
                            }

                            list1.add(strings);
                        }

                        conditions.add(list1);
                    }

                    for (Column column : table.getColumns()) {

                        for (List<String[]> list1 : conditions) {
                            int finished = 0;

                            for (String[] strings : list1) {

                                if (column.getContent().containsKey(strings[0])) {
                                    if (column.getContent(strings[0]).toString().equals(strings[1])) {
                                        finished++;
                                    } else if (column.getContent(strings[0]) == null && strings[1].equalsIgnoreCase("null")) {
                                        finished++;
                                    }
                                } else if (strings[1].equalsIgnoreCase("null")) {
                                    finished++;
                                }

                            }

                            if (finished == list1.size()) {
                                columns.add(column);
                                break;
                            }
                        }

                    }

                    if (limit != -1) {
                        List<Column> list1 = new ArrayList<>();

                        int current = 0;

                        for (Column column : columns) {
                            if (current >= limit) {
                                break;
                            }

                            list1.add(column);

                            current++;
                        }

                        columns = new ArrayList<>(list1);
                    }

                    remote.send(JFQL.getInstance().getBuilder().buildAnswer(columns, structure));
                } else {
                    List<String> list;

                    if (!values.contains(table.getPrimary())) {
                        list = new ArrayList<>();
                        list.add(table.getPrimary());
                        list.addAll(values);
                    } else {
                        list = new ArrayList<>(values);
                    }

                    String[] structure = new String[list.size()];
                    int index = 0;

                    for (String var : list) {
                        structure[index] = var;
                        index++;
                    }

                    List<Column> columns = new ArrayList<>(table.getColumns());

                    if (limit != -1) {
                        ArrayList<Column> list1 = new ArrayList<>();

                        int current = 0;

                        for (Column column : columns) {
                            if (current >= limit) {
                                break;
                            }

                            list1.add(column);
                            current++;
                        }

                        columns = new ArrayList<>(list1);
                    }

                    remote.send(JFQL.getInstance().getBuilder().buildAnswer(columns, structure));
                }

                return true;
            }

            remote.send(JFQL.getInstance().getBuilder().buildSyntax());
        } else {

            if (arguments.containsKey("VALUE") && arguments.containsKey("FROM")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));
                int limit = -1;

                if (arguments.containsKey("LIMIT")) {
                    limit = JFQL.getInstance().getFormatter().formatInteger(arguments.get("LIMIT"));

                    if (limit <= -1) {
                        JFQL.getInstance().getConsole().logError("Limit can't be smaller than 0!");
                        return true;
                    }
                }


                final Database dataBase = dataBaseHandler.getDataBase(JFQL.getInstance().getDBSession().get(user.getName()));

                if (dataBase.getTable(name) == null) {
                    JFQL.getInstance().getConsole().logError("Table '" + name + "' doesn't exists!");
                    return true;
                }

                final Table table = dataBase.getTable(name);

                List<String> values = new ArrayList<>();

                for (String var : arguments.get("VALUE")) {
                    if (var.equals("*")) {
                        values = table.getStructure();
                        break;
                    }

                    values.add(var);
                }

                if (values.isEmpty()) {
                    JFQL.getInstance().getConsole().logError("Please enter values to select!");
                    return true;
                }


                for (String var : values) {
                    if (!table.getStructure().contains(var)) {
                        JFQL.getInstance().getConsole().logError("Unknown key!");
                        return true;
                    }
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    Column column = table.getColumn(JFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));

                    if (column == null) {
                        JFQL.getInstance().getConsole().logError("Unknown primary key!");
                        return true;
                    }

                    List<String> list;

                    if (!values.contains(table.getPrimary())) {
                        list = new ArrayList<>();
                        list.add(table.getPrimary());
                        list.addAll(values);
                    } else {
                        list = new ArrayList<>(values);
                    }

                    String[] structure = new String[list.size()];
                    int index = 0;

                    for (String var : list) {
                        structure[index] = var;
                        index++;
                    }

                    final TablePrinter tablePrinter = new TablePrinter(structure.length, structure);

                    String[] strings = new String[structure.length];
                    index = 0;

                    for (String str : structure) {
                        if (column.getContent(str) != null) {
                            strings[index] = column.getContent(str).toString();
                        } else strings[index] = "null";

                        index++;
                    }

                    if (limit != 0) {
                        tablePrinter.addRow(strings);
                    }

                    tablePrinter.print();
                } else if (arguments.containsKey("WHERE")) {
                    List<String> list;

                    if (!values.contains(table.getPrimary())) {
                        list = new ArrayList<>();
                        list.add(table.getPrimary());
                        list.addAll(values);
                    } else {
                        list = new ArrayList<>(values);
                    }

                    String[] structure = new String[list.size()];
                    int index = 0;

                    for (String var : list) {
                        structure[index] = var;
                        index++;
                    }

                    final TablePrinter tablePrinter = new TablePrinter(structure.length, structure);

                    List<Column> columns = new ArrayList<>();

                    String[] where = JFQL.getInstance().getFormatter().formatString(arguments.get("WHERE")).replace(" or ", " OR ").split(" OR ");
                    List<List<String[]>> conditions = new ArrayList<>();

                    for (int j = 0; j < where.length; j++) {
                        String[] args = where[j].replace(" and ", " AND ").split(" AND ");

                        List<String[]> list1 = new ArrayList<>();

                        for (int i = 0; i < args.length; i++) {
                            String[] strings = args[i].split(" = ");

                            strings[0] = strings[0].replace("'", "");
                            strings[1] = strings[1].replace("'", "");

                            if (!table.getStructure().contains(strings[0])) {
                                JFQL.getInstance().getConsole().logError("Unknown key!");
                                return true;
                            }

                            list1.add(strings);
                        }

                        conditions.add(list1);
                    }

                    for (Column column : table.getColumns()) {

                        for (List<String[]> list1 : conditions) {
                            int finished = 0;

                            for (String[] strings : list1) {

                                if (column.getContent().containsKey(strings[0])) {
                                    if (column.getContent(strings[0]).toString().equals(strings[1])) {
                                        finished++;
                                    } else if (column.getContent(strings[0]) == null && strings[1].equalsIgnoreCase("null")) {
                                        finished++;
                                    }
                                } else if (strings[1].equalsIgnoreCase("null")) {
                                    finished++;
                                }

                            }

                            if (finished == list1.size()) {
                                columns.add(column);
                                break;
                            }
                        }

                    }

                    if (limit != -1) {
                        ArrayList<Column> list1 = new ArrayList<>();

                        int current = 0;

                        for (Column column : columns) {
                            if (current >= limit) {
                                break;
                            }

                            list1.add(column);

                            current++;
                        }

                        columns = new ArrayList<>(list1);
                    }

                    for (Column column : columns) {

                        String[] strings = new String[structure.length];
                        index = 0;

                        for (String str : structure) {
                            if (column.getContent(str) != null) {
                                strings[index] = column.getContent(str).toString();
                            } else strings[index] = "null";

                            index++;
                        }

                        tablePrinter.addRow(strings);
                    }

                    tablePrinter.print();
                } else {
                    List<String> list;

                    if (!values.contains(table.getPrimary())) {
                        list = new ArrayList<>();
                        list.add(table.getPrimary());
                        list.addAll(values);
                    } else {
                        list = new ArrayList<>(values);
                    }

                    String[] structure = new String[list.size()];
                    int index = 0;

                    for (String var : list) {
                        structure[index] = var;
                        index++;
                    }

                    final TablePrinter tablePrinter = new TablePrinter(structure.length, structure);

                    List<Column> columns = new ArrayList<>(table.getColumns());

                    if (limit != -1) {
                        List<Column> list1 = new ArrayList<>();

                        int current = 0;

                        for (Column column : columns) {
                            if (current >= limit) {
                                break;
                            }

                            list1.add(column);

                            current++;
                        }

                        columns = new ArrayList<>(list1);
                    }

                    for (Column column : columns) {

                        String[] strings = new String[structure.length];
                        index = 0;

                        for (String str : structure) {
                            if (column.getContent(str) != null) {
                                strings[index] = column.getContent(str).toString();
                            } else strings[index] = "null";

                            index++;
                        }

                        tablePrinter.addRow(strings);
                    }

                    tablePrinter.print();
                }

                return true;
            }

            JFQL.getInstance().getConsole().logError("Unknown syntax!");
        }

        return false;
    }
}
