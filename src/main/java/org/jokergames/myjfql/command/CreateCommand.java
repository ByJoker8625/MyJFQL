package org.jokergames.myjfql.command;

import org.jokergames.myjfql.command.executor.ConsoleExecutor;
import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.database.Table;
import org.jokergames.myjfql.script.Script;
import org.jokergames.myjfql.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Janick
 */

public class CreateCommand extends Command {

    public CreateCommand() {
        super("CREATE", List.of("COMMAND", "SCRIPT", "DATABASE", "TABLE", "STRUCTURE", "INTO", "PRIMARY-KEY", "SRC"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseService dataBaseService = MyJFQL.getInstance().getDatabaseService();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.create")) {
                return false;
            }

            if (arguments.containsKey("SCRIPT") && arguments.containsKey("SRC")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));
                String src = MyJFQL.getInstance().getFormatter().formatString(arguments.get("SRC"));

                if (!user.hasPermission("execute.create.script.*") && !user.hasPermission("execute.create.script." + name)) {
                    return false;
                }

                final Script script = new Script(name);
                script.formatCommands(src);

                remote.sendSuccess();
                MyJFQL.getInstance().getScriptService().saveScript(script);
                return true;
            }


            if (arguments.containsKey("DATABASE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (!user.hasPermission("execute.create.database.*") && !user.hasPermission("execute.create.database." + name)) {
                    return false;
                }

                if (dataBaseService.getDataBase(name) != null) {
                    remote.sendError("Database already exists!");
                    return true;
                }

                dataBaseService.saveDataBase(new Database(name));
                remote.sendSuccess();
                return true;
            }

            if (arguments.containsKey("TABLE") && arguments.containsKey("STRUCTURE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("TABLE"));

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
                    base = MyJFQL.getInstance().getFormatter().formatString(arguments.get("INTO"));
                } else {
                    base = MyJFQL.getInstance().getDBSession().get(user.getName());
                }

                if (!user.hasPermission("execute.use.*") && !user.hasPermission("execute.use." + base)) {
                    return false;
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    primaryKey = MyJFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY"));
                } else {
                    primaryKey = structure.get(0);
                }

                if (dataBaseService.getDataBase(base) == null) {
                    remote.sendError("Database doesn't exists!");
                    return true;
                }

                final Database dataBase = dataBaseService.getDataBase(base);

                if (dataBase.getTable(name) != null) {
                    remote.sendError("Table already exists!");
                    return true;
                }


                remote.sendSuccess();
                dataBase.addTable(new Table(name, structure, primaryKey));
                dataBaseService.saveDataBase(dataBase);
                return true;
            }


            remote.sendSyntax();
        } else {
            ConsoleExecutor console = (ConsoleExecutor) executor;

            if (arguments.containsKey("SCRIPT")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));

                if (name.equals("")) {
                    console.sendError("Bad script name!");
                    return true;
                }

                if (MyJFQL.getInstance().getScriptService().getScript(name) != null) {
                    System.out.print("[" + MyJFQL.getInstance().getConsole().getTime() + "] WARNING: Script already exists. Do you want to overwrite it [y/n]: ");

                    if (!MyJFQL.getInstance().getConsole().read().equals("y")) {
                        return true;
                    }
                }

                final Script script = new Script(name);

                if (arguments.containsKey("SRC")) {
                    script.formatCommands(MyJFQL.getInstance().getFormatter().formatString(arguments.get("SRC")));
                } else {
                    StringBuilder builder = new StringBuilder();

                    MyJFQL.getInstance().getConsole().log("Script: \"" + name + "\" {");
                    System.out.print(": ");

                    final Scanner scanner = MyJFQL.getInstance().getConsole().getScanner();
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

                console.sendInfo("Script '" + name + "' was created.");
                console.sendInfo("To invoke this enter: 'INVOKE SCRIPT " + name + "'");
                MyJFQL.getInstance().getScriptService().saveScript(script);
                return true;
            }

            if (arguments.containsKey("DATABASE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (dataBaseService.getDataBase(name) != null) {
                    console.sendError("Database '" + name + "' was not found!");
                    return true;
                }

                dataBaseService.saveDataBase(new Database(name));
                console.sendInfo("Database '" + name + "' was created.");
                return true;
            }

            if (arguments.containsKey("TABLE") && arguments.containsKey("STRUCTURE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("TABLE"));
                List<String> structure = new ArrayList<>();

                for (String str : arguments.get("STRUCTURE")) {
                    structure.add(str.replace("'", ""));
                }

                String base;
                String primaryKey;

                if (arguments.containsKey("INTO")) {
                    base = MyJFQL.getInstance().getFormatter().formatString(arguments.get("INTO"));
                } else {
                    base = MyJFQL.getInstance().getDBSession().get(user.getName());
                }

                if (arguments.containsKey("PRIMARY-KEY")) {
                    primaryKey = MyJFQL.getInstance().getFormatter().formatString(arguments.get("PRIMARY-KEY"));
                } else {
                    primaryKey = structure.get(0);
                }

                if (dataBaseService.getDataBase(base) == null) {
                    console.sendError("Database '" + name + "' was not found!");
                    return true;
                }

                final Database dataBase = dataBaseService.getDataBase(base);

                if (dataBase.getTable(name) != null) {
                    console.sendError("Table '" + name + "' already exists!");
                    return true;
                }


                console.sendInfo("Table '" + name + "' was created.");
                dataBase.addTable(new Table(name, structure, primaryKey));
                dataBaseService.saveDataBase(dataBase);
                return true;
            }

            console.sendError("Unknown syntax!");
        }

        return true;
    }
}
