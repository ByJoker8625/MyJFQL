package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.BackupService;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandHandler
public class BackupCommand extends ConsoleCommand {

    public BackupCommand() {
        super("backup", Arrays.asList("COMMAND", "CREATE", "DELETE", "LOAD", "FETCH", "LIST", "RE-BACKUP", "DISPLAY"));
    }

    @Override
    public void handleConsoleCommand(ConsoleCommandSender sender, Map<String, List<String>> args) {
        final BackupService backupService = MyJFQL.getInstance().getDatabaseBackupService();

        if (args.containsKey("CREATE")) {
            final String name = formatString(args.get("CREATE"));

            if (name == null) {
                sender.sendError("Undefined backup!");
                return;
            }

            if (backupService.existsBackup(name)) {
                sender.sendError("Backup already exists!");
                return;
            }

            try {
                backupService.createBackup(name);
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

            if (!backupService.existsBackup(name)) {
                sender.sendError("Backup doesn't exists!");
                return;
            }

            try {
                backupService.deleteBackup(name);
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

            if (!backupService.existsBackup(name)) {
                sender.sendError("Backup doesn't exists!");
                return;
            }

            if (args.containsKey("RE-BACKUP")) {
                final String preBackupName = "TMP-B-" + name + "-" + System.currentTimeMillis();

                if (!backupService.existsBackup(preBackupName))
                    backupService.createBackup(preBackupName);
            }

            try {
                backupService.loadBackup(name);
            } catch (Exception ex) {
                sender.sendError("Failed to load backup!");
                return;
            }

            sender.sendSuccess();
            return;
        }

        if (args.containsKey("FETCH")) {
            final String information = formatString(args.get("FETCH"));

            if (information == null) {
                sender.sendError("Undefined user, password and host!");
                return;
            }

            String user;
            String password;
            String host;

            try {
                final String[] connectionInformation = information.split("@");
                final String[] userInformation = connectionInformation[0].split(":");

                user = userInformation[0];
                password = userInformation[1];
                host = connectionInformation[1];
            } catch (Exception ex) {
                sender.sendError("Unknown fetch information format!");
                return;
            }

            if (args.containsKey("RE-BACKUP")) {
                final String preBackupName = "TMP-B-" + user + "-" + System.currentTimeMillis();

                if (!backupService.existsBackup(preBackupName))
                    backupService.createBackup(preBackupName);
            }

            try {
                backupService.fetchBackup(user, password, host);
            } catch (Exception ex) {
                sender.sendError("Failed to fetch backup!");
                return;
            }

            sender.sendSuccess();
            return;
        }

        if (args.containsKey("DISPLAY")) {
            final String name = formatString(args.get("DISPLAY"));

            if (name == null) {
                sender.sendError("Undefined name!");
                return;
            }

            if (!backupService.existsBackup(name)) {
                sender.sendError("Backup doesn't exists!");
                return;
            }

            final File backup = new File("backup/" + name);
            final File[] files = backup.listFiles();

            if (files == null) {
                sender.sendError("Failed to load backup!");
                return;
            }

            sender.sendResult(Arrays.stream(files).map(File::getName).collect(Collectors.toList()), new String[]{"Backup"});
            return;
        }

        if (args.containsKey("LIST")) {
            sender.sendResult(backupService.getBackups(), new String[]{"Backup"});
            return;
        }

        sender.sendSyntax();
    }

}
