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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class InsertCommand extends Command {

    public InsertCommand() {
        super("INSERT", List.of("COMMAND", "INTO", "WHERE", "KEY", "VALUE", "PRIMARY-KEY"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseHandler dataBaseHandler = JFQL.getInstance().getDataBaseHandler();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.insert")) {
                return false;
            }

            final Database dataBase = dataBaseHandler.getDataBase(JFQL.getInstance().getDbSession().get(user.getName()));

            if (arguments.containsKey("INTO") && arguments.containsKey("KEY") && arguments.containsKey("VALUE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("INTO"));

                List<String> keys = new ArrayList<>();

                for (String str : arguments.get("KEY")) {
                    keys.add(str.replace("'", ""));
                }

                List<String> values = new ArrayList<>();

                for (String str : arguments.get("VALUE")) {
                    values.add(str.replace("'", ""));
                }

                if (dataBase.getTable(name) == null) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Table doesn't exists!")));
                    return true;
                }

                if (keys.isEmpty() || values.isEmpty()) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Keys or values are empty!")));
                    return true;
                }

                if (keys.size() > values.size() || values.size() > keys.size()) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Enter suitable keys and values!")));
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
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown key!")));
                    return true;
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    Column column = table.getColumn(JFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));

                    if (column == null) {
                        column = new Column();
                        column.setContent(content);
                    } else {
                        column.getContent().putAll(content);
                    }

                    if (column.getContent(table.getPrimary()) == null) {
                        column.putContent(table.getPrimary(), JFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));
                    }

                    table.addColumn(column);
                    dataBase.addTable(table);
                    dataBaseHandler.saveDataBase(dataBase);

                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                } else if (arguments.containsKey("WHERE")) {
                    List<Column> columns = null;

                    try {
                        columns = JFQL.getInstance().getConditionHelper().getRequiredColumns(table, arguments.get("WHERE"));
                    } catch (Exception ex) {
                        remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown 'where' error!")));
                        return true;
                    }

                    if (columns == null) {
                        remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown 'where' error!")));
                        return true;
                    }

                    for (Column column : columns) {

                        for (String s : content.keySet()) {
                            if (content.get(s).toString().equalsIgnoreCase("null")) {
                                content.remove(s);
                            } else {
                                column.getContent().put(s, content.get(s));
                            }
                        }

                        table.addColumn(column);
                    }

                    dataBase.addTable(table);
                    dataBaseHandler.saveDataBase(dataBase);

                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                } else {
                    if (content.get(table.getPrimary()) == null) {
                        remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown primary key!")));
                        return true;
                    }

                    Column column = table.getColumn(content.get(table.getPrimary()).toString());

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
                    dataBaseHandler.saveDataBase(dataBase);

                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                }

                return true;
            }

            remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSyntax());
        } else {
            final Database dataBase = dataBaseHandler.getDataBase(JFQL.getInstance().getDbSession().get(user.getName()));

            if (arguments.containsKey("INTO") && arguments.containsKey("KEY") && arguments.containsKey("VALUE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("INTO"));

                List<String> keys = new ArrayList<>();

                for (String str : arguments.get("KEY")) {
                    keys.add(str.replace("'", ""));
                }

                List<String> values = new ArrayList<>();

                for (String str : arguments.get("VALUE")) {
                    values.add(str.replace("'", ""));
                }

                if (dataBase.getTable(name) == null) {
                    JFQL.getInstance().getConsole().logError("Table '" + name + "' doesn't exists!");
                    return true;
                }

                if (keys.isEmpty() || values.isEmpty()) {
                    JFQL.getInstance().getConsole().logError("Please enter keys and values!");
                    return true;
                }

                if (keys.size() > values.size() || values.size() > keys.size()) {
                    JFQL.getInstance().getConsole().logError("Please enter suitable keys and values!");
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
                    JFQL.getInstance().getConsole().logError("Unknown key!");
                    return true;
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    Column column = table.getColumn(JFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));

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
                        column.putContent(table.getPrimary(), JFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY")));
                    }

                    table.addColumn(column);
                    dataBase.addTable(table);
                    dataBaseHandler.saveDataBase(dataBase);

                    JFQL.getInstance().getConsole().logInfo("Insert values into '" + name + "'.");
                } else if (arguments.containsKey("WHERE")) {
                    List<Column> columns = null;

                    try {
                        columns = JFQL.getInstance().getConditionHelper().getRequiredColumns(table, arguments.get("WHERE"));
                    } catch (Exception ex) {
                        JFQL.getInstance().getConsole().logError("Unknown error!");
                        return true;
                    }

                    if (columns == null) {
                        JFQL.getInstance().getConsole().logError("Unknown key!");
                        return true;
                    }

                    for (Column column : columns) {

                        for (String s : content.keySet()) {
                            if (content.get(s).toString().equalsIgnoreCase("null")) {
                                content.remove(s);
                            } else {
                                column.getContent().put(s, content.get(s));
                            }
                        }

                        table.addColumn(column);
                    }

                    dataBase.addTable(table);
                    dataBaseHandler.saveDataBase(dataBase);

                    JFQL.getInstance().getConsole().logInfo("Insert values into '" + name + "'.");
                } else {
                    if (content.get(table.getPrimary()) == null) {
                        JFQL.getInstance().getConsole().logError("Unknown key!");
                        return true;
                    }

                    Column column = table.getColumn(content.get(table.getPrimary()).toString());

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
                    dataBaseHandler.saveDataBase(dataBase);

                    JFQL.getInstance().getConsole().logInfo("Insert values into '" + name + "'.");
                }

                return true;
            }

            JFQL.getInstance().getConsole().logError("Unknown syntax!");
        }

        return true;
    }
}
