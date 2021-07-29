package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.database.Table;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StructureCommand extends Command {

    public StructureCommand() {
        super("structure", Arrays.asList("COMMAND", "ADD", "SET", "REMOVE", "OF", "MARK-PRIMARY", "PRIMARY-KEY"));
    }

    @Override
    public void handleCommand(final CommandSender sender, final Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final Database database = databaseService.getDataBase(MyJFQL.getInstance().getDBSession().get(sender.getName()));

        if (args.containsKey("OF")) {
            final String name = formatString(args.get("OF"));

            if (name == null) {
                sender.sendError("Unknown table!");
                return;
            }

            if (!database.isCreated(name)) {
                sender.sendError("Table doesn't exists!");
                return;
            }

            if (!sender.hasPermission("use.table." + name + "." + database.getName())
                    && !sender.hasPermission("use.table.*." + database.getName())) {
                sender.sendForbidden();
                return;
            }

            final Table table = database.getTable(name);
            final List<String> structure = table.getStructure();
            final String primary = table.getPrimary();

            if (!args.containsKey("ADD") && !args.containsKey("REMOVE") && !args.containsKey("SET") && !args.containsKey("MARK-PRIMARY")) {
                if (args.containsKey("PRIMARY-KEY")) {
                    sender.sendAnswer(Collections.singletonList(primary), new String[]{"Primary"});
                    return;
                }

                sender.sendAnswer(structure, new String[]{"Structure"});
                return;
            }

            if (args.containsKey("ADD")) {
                final String argument = formatString(args.get("ADD"));

                if (structure.contains(argument)) {
                    sender.sendError("Key already contains in structure of table!");
                    return;
                }

                structure.add(argument);

                sender.sendSuccess();

                table.setStructure(structure);
                database.addTable(table);
                databaseService.saveDataBase(database);
                return;
            }

            if (args.containsKey("REMOVE")) {
                final String argument = formatString(args.get("REMOVE"));

                if (!structure.contains(argument)) {
                    sender.sendError("Key doesn't contains in structure of table!");
                    return;
                }

                if (primary.equals(argument)) {
                    sender.sendError("Primary key can't be removed!");
                    return;
                }

                structure.remove(argument);

                sender.sendSuccess();

                table.setStructure(structure);
                database.addTable(table);
                databaseService.saveDataBase(database);
                return;
            }

            if (args.containsKey("MARK-PRIMARY")) {
                final String argument = formatString(args.get("MARK-PRIMARY"));

                if (!structure.contains(argument)) {
                    sender.sendError("Key doesn't contains in structure of table!");
                    return;
                }

                if (primary.equals(argument)) {
                    sender.sendError("Key is already the primary key!");
                    return;
                }

                table.setPrimary(argument);

                sender.sendSuccess();

                database.addTable(table);
                databaseService.saveDataBase(database);
                return;
            }

            if (args.containsKey("SET")) {
                final List<String> newStructure = args.get("SET");

                if (newStructure.size() == 0) {
                    sender.sendError("Structures size cant be 0!");
                    return;
                }

                if (structure.equals(newStructure)) {
                    sender.sendError("Structure is the same as before!");
                    return;
                }

                table.setStructure(newStructure);

                if (args.containsKey("PRIMARY-KEY")) {
                    final String key = formatString(args.get("PRIMARY-KEY"));

                    if (key == null) {
                        sender.sendError("Key can't be null!");
                        return;
                    }

                    if (!structure.contains(key)) {
                        sender.sendError("Unknown key!");
                        return;
                    }

                    table.setPrimary(key);
                } else {
                    table.setPrimary(newStructure.get(0));
                }


                sender.sendSuccess();

                database.addTable(table);
                databaseService.saveDataBase(database);
                return;
            }

            return;
        }

        sender.sendSyntax();
    }
}
