package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.ConsoleExecutor;
import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.core.script.Script;
import de.jokergames.jfql.core.script.ScriptService;
import de.jokergames.jfql.database.Column;
import de.jokergames.jfql.database.Database;
import de.jokergames.jfql.database.DatabaseHandler;
import de.jokergames.jfql.database.Table;
import de.jokergames.jfql.user.User;
import de.jokergames.jfql.util.Sorter;
import de.jokergames.jfql.util.TablePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class SelectCommand extends Command {


    public SelectCommand() {
        super("SELECT", List.of("COMMAND", "SCRIPT", "WHERE", "FROM", "VALUE", "PRIMARY-KEY", "LIMIT", "SORT", "ORDER"), List.of("SEL"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseHandler dataBaseHandler = JFQL.getInstance().getDataBaseHandler();
        final ScriptService scriptService = JFQL.getInstance().getScriptService();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.select")) {
                return false;
            }

            if (arguments.containsKey("SCRIPT")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));


                if (!user.hasPermission("execute.select.script.*") && !user.hasPermission("execute.select.script." + name)) {
                    return false;
                }
                if (scriptService.getScript(name) == null) {
                    remote.sendError("Script doesn't exists!");
                    return true;
                }

                final Script script = scriptService.getScript(name);
                remote.sendAnswer(script.getCommands(), List.of("Queries"));
                return true;
            }

            if (arguments.containsKey("VALUE") && arguments.containsKey("FROM")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));
                int limit = -1;

                Sorter.Type sort = Sorter.Type.CREATION;
                Sorter.Order order = Sorter.Order.ASC;
                String sorter = null;

                if (arguments.containsKey("LIMIT")) {
                    limit = JFQL.getInstance().getFormatter().formatInteger(arguments.get("LIMIT"));

                    if (limit <= -1) {
                        remote.sendError("Limit can't be smaller than 0!");
                        return true;
                    }
                }

                final Database dataBase = dataBaseHandler.getDataBase(JFQL.getInstance().getDbSession().get(user.getName()));

                if (dataBase.getTable(name) == null) {
                    remote.sendError("Database doesn't exists!");
                    return true;
                }

                final Table table = dataBase.getTable(name);

                if (arguments.containsKey("SORT")) {
                    sorter = JFQL.getInstance().getFormatter().formatString(arguments.get("SORT"));

                    if (!table.getStructure().contains(sorter)) {
                        remote.sendError("Unknown key to sort!");
                        return true;
                    }

                    sort = Sorter.Type.CUSTOM;
                }

                if (arguments.containsKey("ORDER") && !arguments.containsKey("SORT")) {
                    remote.sendError("Enter a column to be sorted!");
                    return true;
                }

                if (arguments.containsKey("ORDER")) {

                    try {
                        order = Sorter.Order.valueOf(JFQL.getInstance().getFormatter().formatString(arguments.get("ORDER")).toUpperCase());
                    } catch (Exception ex) {
                        remote.sendError("Unknown order type (DES, ASC)!");
                        return true;
                    }

                }

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
                    remote.sendError("No values to select!");
                    return true;
                }

                for (String var : values) {
                    if (!table.getStructure().contains(var)) {
                        remote.sendError("Unknown key!");
                        return true;
                    }
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    Column column = table.getColumn(JFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));

                    if (column == null) {
                        remote.sendError("Column was not found!");
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

                    remote.sendAnswer(columns, table.getStructure());
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

                    List<Column> columns = null;

                    try {
                        columns = JFQL.getInstance().getConditionHelper().getRequiredColumns(table, arguments.get("WHERE"), sort, sorter, order);
                    } catch (Exception ex) {
                        remote.sendError("Unknown 'where' error!");
                        return true;
                    }

                    if (columns == null) {
                        remote.sendError("Unknown 'where' error!");
                        return true;
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

                    remote.sendAnswer(columns, structure);
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

                    List<Column> columns = new ArrayList<>(table.getColumns(sort, order, sorter));

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

                    remote.sendAnswer(columns, structure);
                }

                return true;
            }

            remote.sendSyntax();
        } else {
            ConsoleExecutor console = (ConsoleExecutor) executor;

            if (arguments.containsKey("SCRIPT")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));

                if (scriptService.getScript(name) == null) {
                    console.sendError("Script '" + name + "' doesn't exists!");
                    return true;
                }

                final Script script = scriptService.getScript(name);
                JFQL.getInstance().getConsole().log(script.toString());
                return true;
            }

            if (arguments.containsKey("VALUE") && arguments.containsKey("FROM")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));
                int limit = -1;

                Sorter.Type sort = Sorter.Type.CREATION;
                Sorter.Order order = Sorter.Order.ASC;
                String sorter = null;

                if (arguments.containsKey("LIMIT")) {
                    limit = JFQL.getInstance().getFormatter().formatInteger(arguments.get("LIMIT"));

                    if (limit <= -1) {
                        console.sendError("Limit can't be smaller than 0!");
                        return true;
                    }
                }


                final Database dataBase = dataBaseHandler.getDataBase(JFQL.getInstance().getDbSession().get(user.getName()));

                if (dataBase.getTable(name) == null) {
                    console.sendError("Table '" + name + "' doesn't exists!");
                    return true;
                }

                final Table table = dataBase.getTable(name);

                if (arguments.containsKey("SORT")) {
                    sorter = JFQL.getInstance().getFormatter().formatString(arguments.get("SORT"));

                    if (!table.getStructure().contains(sorter)) {
                        console.sendError("Unknown key to sort!");
                        return true;
                    }

                    sort = Sorter.Type.CUSTOM;
                }

                if (arguments.containsKey("ORDER") && !arguments.containsKey("SORT")) {
                    console.sendError("Enter a column to be sorted!");
                    return true;
                }

                if (arguments.containsKey("ORDER")) {

                    try {
                        order = Sorter.Order.valueOf(JFQL.getInstance().getFormatter().formatString(arguments.get("ORDER")).toUpperCase());
                    } catch (Exception ex) {
                        console.sendError("Unknown order! Orders: ASC, DEC");
                        return true;
                    }

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
                    console.sendError("Please enter values to select!");
                    return true;
                }


                for (String var : values) {
                    if (!table.getStructure().contains(var)) {
                        console.sendError("Unknown key!");
                        return true;
                    }
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    Column column = table.getColumn(JFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));

                    if (column == null) {
                        console.sendError("Unknown primary key!");
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

                    List<Column> columns = null;

                    try {
                        columns = JFQL.getInstance().getConditionHelper().getRequiredColumns(table, arguments.get("WHERE"), sort, sorter, order);
                    } catch (Exception ex) {
                        console.sendError("Unknown error!");
                        return true;
                    }

                    if (columns == null) {
                        console.sendError("Unknown key!");
                        return true;
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

                    List<Column> columns = new ArrayList<>(table.getColumns(sort, order, sorter));

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

            console.sendError("Unknown syntax!");
        }

        return false;
    }
}
