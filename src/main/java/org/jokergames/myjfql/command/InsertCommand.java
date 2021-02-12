package org.jokergames.myjfql.command;

import org.jokergames.myjfql.command.executor.ConsoleExecutor;
import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Column;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.database.Table;
import org.jokergames.myjfql.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class InsertCommand extends Command {

    public InsertCommand() {
        super("INSERT", List.of("COMMAND", "INTO", "WHERE", "KEY", "VALUE", "PRIMARY-KEY"), List.of("INS"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseService dataBaseService = MyJFQL.getInstance().getDatabaseService();
        final Database dataBase = dataBaseService.getDataBase(MyJFQL.getInstance().getDBSession().get(user.getName()));

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.insert")) {
                return false;
            }

            if (arguments.containsKey("INTO") && arguments.containsKey("KEY") && arguments.containsKey("VALUE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("INTO"));

                List<String> keys = new ArrayList<>();

                for (String str : arguments.get("KEY")) {
                    keys.add(str.replace("'", ""));
                }

                List<String> values = new ArrayList<>();

                for (String str : arguments.get("VALUE")) {
                    values.add(str.replace("'", ""));
                }

                if (dataBase.getTable(name) == null) {
                    remote.sendError("Table doesn't exists!");
                    return true;
                }

                if (keys.isEmpty() || values.isEmpty()) {
                    remote.sendError("Keys or values are empty!");
                    return true;
                }

                if (keys.size() > values.size() || values.size() > keys.size()) {
                    remote.sendError("Enter suitable keys and values!");
                    return true;
                }

                final Table table = dataBase.getTable(name);

                if (!user.hasPermission("execute.insert.database." + dataBase.getName() + ".*") && !user.hasPermission("execute.insert.database." + dataBase.getName() + "." + table.getName())) {
                    return false;
                }

                Map<String, Object> content = new HashMap<>();

                for (int j = 0; j < keys.size(); j++) {
                    content.put(keys.get(j), values.get(j));
                }

                boolean contains = false;

                for (String key : keys) {
                    if (!table.getStructure().contains(key)) {
                        contains = true;
                        break;
                    }
                }

                if (contains) {
                    remote.sendError("Unknown key!");
                    return true;
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    Column column = table.getColumn(MyJFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));

                    for (String s : content.keySet()) {
                        String s1 = content.get(s).toString();

                        if (s1.equals("$++")) {
                            content.put(s, table.getColumns().size() + 1);
                        } else if (s1.equals("$--")) {
                            content.put(s, table.getColumns().size() - 1);
                        }
                    }

                    if (column == null) {
                        column = new Column();
                        column.setContent(content);
                    } else {
                        column.getContent().putAll(content);
                    }

                    if (column.getContent(table.getPrimary()) == null) {
                        column.putContent(table.getPrimary(), MyJFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));
                    }

                    table.addColumn(column);
                    dataBase.addTable(table);
                    dataBaseService.saveDataBase(dataBase);

                    remote.sendSuccess();
                } else if (arguments.containsKey("WHERE")) {
                    List<Column> columns = null;

                    try {
                        columns = MyJFQL.getInstance().getConditionHelper().getRequiredColumns(table, arguments.get("WHERE"));
                    } catch (Exception ex) {
                        remote.sendError("Unknown 'where' error!");
                        return true;
                    }

                    if (columns == null) {
                        remote.sendError("Unknown 'where' error!");
                        return true;
                    }

                    for (Column column : columns) {

                        for (String s : content.keySet()) {
                            if (content.get(s).toString().equalsIgnoreCase("null")) {
                                content.remove(s);
                            } else {
                                Object obj = content.get(s);

                                if (obj.toString().equals("$++")) {
                                    int j = table.getColumns().size() + 1;
                                    column.putContent(j + "", obj);
                                } else if (obj.toString().equals("$--")) {
                                    int j = table.getColumns().size() - 1;
                                    column.putContent(j + "", obj);
                                } else
                                    column.getContent().put(s, content.get(s));
                            }
                        }

                        table.addColumn(column);
                    }

                    dataBase.addTable(table);
                    dataBaseService.saveDataBase(dataBase);

                    remote.sendSuccess();
                } else {
                    if (content.get(table.getPrimary()) == null) {
                        remote.sendError("Unknown primary key!");
                        return true;
                    }

                    Column column = table.getColumn(content.get(table.getPrimary()).toString());

                    for (String s : content.keySet()) {
                        if (content.get(s).equals("$++")) {
                            content.put(s, table.getColumns().size() + 1);
                        } else if (content.get(s).equals("$--")) {
                            content.put(s, table.getColumns().size() - 1);
                        }
                    }

                    if (column == null) {
                        column = new Column();
                        column.setContent(content);
                    } else {
                        column.getContent().putAll(content);
                    }

                    if (column.getContent(table.getPrimary()) == null) {
                        column.putContent(table.getPrimary(), content.get(table.getPrimary()).toString());
                    }


                    table.addColumn(column);
                    dataBase.addTable(table);
                    dataBaseService.saveDataBase(dataBase);

                    remote.sendSuccess();
                }

                return true;
            }

            remote.sendSyntax();
        } else {
            ConsoleExecutor console = (ConsoleExecutor) executor;

            if (arguments.containsKey("INTO") && arguments.containsKey("KEY") && arguments.containsKey("VALUE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("INTO"));

                List<String> keys = new ArrayList<>();

                for (String str : arguments.get("KEY")) {
                    keys.add(str.replace("'", ""));
                }

                List<String> values = new ArrayList<>();

                for (String str : arguments.get("VALUE")) {
                    values.add(str.replace("'", ""));
                }

                if (dataBase.getTable(name) == null) {
                    console.sendError("Table '" + name + "' doesn't exists!");
                    return true;
                }

                if (keys.isEmpty() || values.isEmpty()) {
                    console.sendError("Please enter keys and values!");
                    return true;
                }

                if (keys.size() > values.size() || values.size() > keys.size()) {
                    console.sendError("Please enter suitable keys and values!");
                    return true;
                }

                final Table table = dataBase.getTable(name);

                Map<String, Object> content = new HashMap<>();

                for (int j = 0; j < keys.size(); j++) {
                    content.put(keys.get(j), values.get(j));
                }

                boolean contains = false;

                for (String key : keys) {
                    if (!table.getStructure().contains(key)) {
                        contains = true;
                        break;
                    }
                }

                if (contains) {
                    console.sendError("Unknown key!");
                    return true;
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    Column column = table.getColumn(MyJFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));

                    for (String s : content.keySet()) {
                        if (content.get(s).equals("$++")) {
                            content.put(s, table.getColumns().size() + 1);
                        } else if (content.get(s).equals("$--")) {
                            content.put(s, table.getColumns().size() - 1);
                        }
                    }

                    if (column == null) {
                        column = new Column();
                        column.setContent(content);
                    } else {
                        for (String s : content.keySet()) {
                            if (content.get(s).toString().equalsIgnoreCase("null")) {
                                content.remove(s);
                            } else {
                                column.getContent().put(s, content.get(s));
                            }
                        }

                        table.addColumn(column);
                    }

                    if (column.getContent(table.getPrimary()) == null) {
                        column.putContent(table.getPrimary(), MyJFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));
                    }

                    table.addColumn(column);
                    dataBase.addTable(table);
                    dataBaseService.saveDataBase(dataBase);

                    console.sendInfo("Insert values into '" + name + "'.");
                } else if (arguments.containsKey("WHERE")) {
                    List<Column> columns = null;

                    try {
                        columns = MyJFQL.getInstance().getConditionHelper().getRequiredColumns(table, arguments.get("WHERE"));
                    } catch (Exception ex) {
                        console.sendError("Unknown error!");
                        return true;
                    }

                    if (columns == null) {
                        console.sendError("Unknown key!");
                        return true;
                    }

                    for (Column column : columns) {

                        for (String s : content.keySet()) {
                            if (content.get(s).toString().equalsIgnoreCase("null")) {
                                content.remove(s);
                            } else {
                                Object obj = content.get(s);

                                if (obj.toString().equals("$++")) {
                                    int j = table.getColumns().size() + 1;
                                    column.putContent(j + "", obj);
                                } else if (obj.toString().equals("$--")) {
                                    int j = table.getColumns().size() - 1;
                                    column.putContent(j + "", obj);
                                } else
                                    column.getContent().put(s, content.get(s));
                            }
                        }

                        table.addColumn(column);
                    }

                    dataBase.addTable(table);
                    dataBaseService.saveDataBase(dataBase);

                    console.sendInfo("Insert values into '" + name + "'.");
                } else {
                    if (content.get(table.getPrimary()) == null) {
                        console.sendError("Unknown key!");
                        return true;
                    }

                    Column column = table.getColumn(content.get(table.getPrimary()).toString());

                    for (String s : content.keySet()) {
                        if (content.get(s).equals("$++")) {
                            content.put(s, table.getColumns().size() + 1);
                        } else if (content.get(s).equals("$--")) {
                            content.put(s, table.getColumns().size() - 1);
                        }
                    }

                    if (column == null) {
                        column = new Column();
                        column.setContent(content);
                    } else {
                        for (String s : content.keySet()) {
                            if (content.get(s).toString().equalsIgnoreCase("null")) {
                                content.remove(s);
                            } else {
                                column.getContent().put(s, content.get(s));
                            }
                        }

                        table.addColumn(column);
                    }

                    if (column.getContent(table.getPrimary()) == null) {
                        column.putContent(table.getPrimary(), content.get(table.getPrimary()).toString());
                    }

                    table.addColumn(column);
                    dataBase.addTable(table);
                    dataBaseService.saveDataBase(dataBase);

                    console.sendInfo("Insert values into '" + name + "'.");
                }

                return true;
            }

            console.sendError("Unknown syntax!");
        }

        return true;
    }
}
