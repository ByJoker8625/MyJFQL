package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.DatabaseBackupService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BackupCommand extends ConsoleCommand {

    public BackupCommand() {
        super("backup", Arrays.asList("COMMAND", "CREATE", "DELETE", "LOAD", "LIST", "RE-BACKUP"));
    }

    @Override
    public void handleConsoleCommand(final ConsoleCommandSender sender, final Map<String, List<String>> args) {
        final DatabaseBackupService databaseBackupService = MyJFQL.getInstance().getDatabaseBackupService();

        if (args.containsKey("CREATE")) {
            final String name = formatString(args.get("CREATE"));

            if (name == null) {
                sender.sendError("Undefined name!");
                return;
            }

            if (databaseBackupService.isCreated(name)) {
                sender.sendError("Backup already exists!");
                return;
            }

            try {
                databaseBackupService.createBackup(name);
            } catch (Exception ex) {
                ex.printStackTrace();
                sender.sendError("Backup failed!");
                return;
            }

            sender.sendSuccess();
            return;
        }

        if (args.containsKey("DELETE")) {
            final String name = formatString(args.get("DELETE"));

            if (name == null) {
                sender.sendError("Undefined name!");
                return;
            }

            if (!databaseBackupService.isCreated(name)) {
                sender.sendError("Backup doesn't exists!");
                return;
            }

            try {
                databaseBackupService.deleteBackup(name);
            } catch (Exception ex) {
                sender.sendError("Failed to delete backup!");
                return;
            }

            sender.sendSuccess();
            return;
        }

        if (args.containsKey("LOAD")) {
            final String name = formatString(args.get("LOAD"));

            if (name == null) {
                sender.sendError("Undefined name!");
                return;
            }

            if (!databaseBackupService.isCreated(name)) {
                sender.sendError("Backup doesn't exists!");
                return;
            }

            if (args.containsKey("RE-BACKUP")) {
                final String preBackupName = String.valueOf(System.currentTimeMillis());

                if (!databaseBackupService.isCreated(preBackupName))
                    databaseBackupService.createBackup(preBackupName);
            }

            try {
                databaseBackupService.loadBackup(name);
            } catch (Exception ex) {
                sender.sendError("Failed to load backup!");
                return;
            }

            sender.sendSuccess();
            return;
        }

        if (args.containsKey("LIST")) {
            sender.sendAnswer(databaseBackupService.getBackups(), new String[]{"Backup"});
            return;
        }

        sender.sendSyntax();
    }

}
