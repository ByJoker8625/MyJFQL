package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.core.script.Script;
import de.jokergames.jfql.database.Database;
import de.jokergames.jfql.database.DatabaseHandler;
import de.jokergames.jfql.database.Table;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Janick
 */

public class CreateCommand extends Command {

    public CreateCommand() {
        super("CREATE", List.of("COMMAND", "SCRIPT", "DATABASE", "TABLE", "STRUCTURE", "INTO", "PRIMARY-KEY", "SRC"), List.of("CRE"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseHandler dataBaseHandler = JFQL.getInstance().getDataBaseHandler();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.create")) {
                return false;
            }

            if (arguments.containsKey("DATABASE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (!user.hasPermission("execute.create.database.*") && !user.hasPermission("execute.create.database." + name)) {
                    return false;
                }

                if (dataBaseHandler.getDataBase(name) != null) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Database already exists!")));
                    return true;
                }

                dataBaseHandler.saveDataBase(new Database(name));
                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                return true;
            }

            if (arguments.containsKey("TABLE") && arguments.containsKey("STRUCTURE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("TABLE"));

                if (!user.hasPermission("execute.create.table.*") && !user.hasPermission("execute.create.table." + name)) {
                    return false;
                }

                List<String> structure = new ArrayList<>();

                for (String str : arguments.get("STRUCTURE")) {
                    structure.add(str.replace("'", ""));
                }

                String base;
                String primaryKey;

                if (arguments.containsKey("INTO")) {
                    base = JFQL.getInstance().getFormatter().formatString(arguments.get("INTO"));
                } else {
                    base = JFQL.getInstance().getDbSession().get(user.getName());
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    primaryKey = JFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY"));
                } else {
                    primaryKey = structure.get(0);
                }

                if (dataBaseHandler.getDataBase(base) == null) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Database doesn't exists!")));
                    return true;
                }

                final Database dataBase = dataBaseHandler.getDataBase(base);

                if (dataBase.getTable(name) != null) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Table already exists!")));
                    return true;
                }


                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                dataBase.addTable(new Table(name, structure, primaryKey));
                dataBaseHandler.saveDataBase(dataBase);
                return true;
            }

            if (arguments.containsKey("SCRIPT") && arguments.containsKey("SRC")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));
                String src = JFQL.getInstance().getFormatter().formatString(arguments.get("SRC"));

                if (!user.hasPermission("execute.create.script.*") && !user.hasPermission("execute.create.script." + name)) {
                    return false;
                }


                final Script script = new Script(name);
                script.formatCommands(src);

                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                JFQL.getInstance().getScriptService().saveScript(script);
                return true;
            }

            remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSyntax());
        } else {
            if (arguments.containsKey("DATABASE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (dataBaseHandler.getDataBase(name) != null) {
                    JFQL.getInstance().getConsole().logError("Database '" + name + "' was not found!");
                    return true;
                }

                dataBaseHandler.saveDataBase(new Database(name));
                JFQL.getInstance().getConsole().logInfo("Database '" + name + "' was created.");
                return true;
            }

            if (arguments.containsKey("TABLE") && arguments.containsKey("STRUCTURE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("TABLE"));
                List<String> structure = new ArrayList<>();

                for (String str : arguments.get("STRUCTURE")) {
                    structure.add(str.replace("'", ""));
                }

                String base;
                String primaryKey;

                if (arguments.containsKey("INTO")) {
                    base = JFQL.getInstance().getFormatter().formatString(arguments.get("INTO"));
                } else {
                    base = JFQL.getInstance().getDbSession().get(user.getName());
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    primaryKey = JFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY"));
                } else {
                    primaryKey = structure.get(0);
                }

                if (dataBaseHandler.getDataBase(base) == null) {
                    JFQL.getInstance().getConsole().logError("Database '" + name + "' was not found!");
                    return true;
                }

                final Database dataBase = dataBaseHandler.getDataBase(base);

                if (dataBase.getTable(name) != null) {
                    JFQL.getInstance().getConsole().logError("Table '" + name + "' already exists!");
                    return true;
                }


                JFQL.getInstance().getConsole().logInfo("Table '" + name + "' was created.");
                dataBase.addTable(new Table(name, structure, primaryKey));
                dataBaseHandler.saveDataBase(dataBase);
                return true;
            }

            if (arguments.containsKey("SCRIPT")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));

                final Script script = new Script(name);

                if (arguments.containsKey("SRC")) {
                    script.formatCommands(JFQL.getInstance().getFormatter().formatString(arguments.get("SRC")));
                } else {
                    StringBuilder builder = new StringBuilder();

                    JFQL.getInstance().getConsole().log("Script: \"" + name + "\" {");
                    System.out.print(": ");

                    final Scanner scanner = JFQL.getInstance().getConsole().getScanner();
                    {
                        String scanned;

                        while (!(scanned = scanner.nextLine()).equals("}")) {
                            if (!scanned.endsWith(";")) {
                                scanned += ";";
                            }

                            builder.append(scanned);
                            System.out.print(": ");
                        }
                    }
                    script.formatCommands(builder.toString());
                }

                JFQL.getInstance().getConsole().logInfo("Script '" + name + "' was created.");
                JFQL.getInstance().getConsole().logInfo("To invoke this enter: 'INVOKE SCRIPT " + name + "'");
                JFQL.getInstance().getScriptService().saveScript(script);
                return true;
            }

            JFQL.getInstance().getConsole().logError("Unknown syntax!");
        }

        return true;
    }
}
