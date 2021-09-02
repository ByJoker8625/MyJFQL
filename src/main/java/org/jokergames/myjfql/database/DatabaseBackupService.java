package org.jokergames.myjfql.database;

import org.apache.commons.io.FileUtils;
import org.jokergames.jfql.connection.Connection;
import org.jokergames.jfql.exception.ConnectorException;
import org.jokergames.jfql.util.Column;
import org.jokergames.jfql.util.Result;
import org.jokergames.jfql.util.User;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.util.FileFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DatabaseBackupService {

    private final DatabaseService databaseService;

    public DatabaseBackupService(final DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void createBackup(final String name) {
        final File folder = new File("backup");
        final File file = new File("backup/" + name);

        if (!folder.exists())
            folder.mkdir();

        if (!file.exists())
            file.mkdir();

        databaseService.update(new File("backup/" + name));
    }

    public void deleteBackup(final String name) throws IOException {
        FileUtils.deleteDirectory(new File("backup/" + name));
    }

    public void loadBackup(final String name) {
        databaseService.load(new File("backup/" + name));
        databaseService.update();
    }

    public void fetchBackup(final String user, final String password, final String host) throws ConnectorException {
        final Connection connection = new Connection(host, new User(user, password));
        connection.connect();

        final DatabaseService databaseService = new DatabaseService(MyJFQL.getInstance().getConfigService().getFactory());

        for (final String databaseName : connection.query("list databases").getColumns().stream().map(Column::getString).collect(Collectors.toList())) {
            try {
                List<String> tables = connection.query("list tables from %", databaseName).getColumns().stream().map(Column::getString).collect(Collectors.toList());
                Database database = new Database(databaseName);

                if (tables.size() != 0) {
                    connection.query("use database " + databaseName);

                    for (String tableName : tables) {
                        try {
                            String primary = null;

                            try {
                                primary = connection.query("structure of % primary-key", tableName).getColumns().get(0).getString();
                            } catch (Exception ex) {
                            }

                            Result result = connection.query("select value * from " + tableName);

                            if (primary == null) {
                                primary = result.getStructureList().get(0);
                            }

                            Table table = new Table(tableName, result.getStructureList(), primary);

                            for (Column column : result.getColumns()) {
                                org.jokergames.myjfql.database.Column col = new org.jokergames.myjfql.database.Column(column.getJsonObject().getJSONObject("content").toMap());
                                col.setCreation(column.getCreation());
                                table.addColumn(col);
                            }

                            database.addTable(table);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                databaseService.saveDataBase(database);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        final File databaseSpace = new File("backup/TMP-F-" + user + "-" + System.currentTimeMillis());

        if (!databaseSpace.exists())
            databaseSpace.mkdir();

        databaseService.update(databaseSpace);

        this.databaseService.load(databaseSpace);
        this.databaseService.update();
    }

    public boolean isCreated(final String name) {
        return new File("backup/" + name).exists();
    }

    public List<String> getBackups() throws NullPointerException {
        return Arrays.stream(Objects.requireNonNull(new File("backup").listFiles())).map(File::getName).collect(Collectors.toList());
    }


}
