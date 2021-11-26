package de.byjoker.myjfql.database;

import de.byjoker.jfql.connection.Connection;
import de.byjoker.jfql.connection.JFQLConnection;
import de.byjoker.jfql.exception.ConnectorException;
import de.byjoker.jfql.util.Column;
import de.byjoker.jfql.util.Result;
import de.byjoker.jfql.util.User;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BackupServiceImpl implements BackupService {

    private final DatabaseService databaseService;

    public BackupServiceImpl(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void createBackup(String name) {
        final File folder = new File("backup");
        final File file = new File("backup/" + name);

        if (!folder.exists())
            folder.mkdir();

        if (!file.exists())
            file.mkdir();

        databaseService.updateAll(file);
    }

    @Override
    public void deleteBackup(String name) {
        try {
            FileUtils.deleteDirectory(new File("backup/" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadBackup(String name) {
        databaseService.loadAll(new File("backup/" + name));
        databaseService.updateAll();
    }

    @Override
    public void fetchBackup(String user, String password, String host) throws ConnectorException {
        final Connection connection = new JFQLConnection(host, new User(user, password));
        connection.connect();

        final DatabaseService databaseService = new DatabaseServiceImpl();

        for (String databaseName : connection.query("list databases").getColumns().stream().map(Column::getString).collect(Collectors.toList())) {
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
                                primary = result.getStructure().get(0);
                            }

                            Table table = new Table(tableName, result.getStructure(), primary);

                            for (Column column : result.getColumns()) {
                                de.byjoker.myjfql.database.Column col = new de.byjoker.myjfql.database.Column(new JSONObject(column.toString()).getJSONObject("content").toMap());
                                col.setCreation(column.getCreation());
                                table.addColumn(col);
                            }

                            database.saveTable(table);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                databaseService.saveDatabase(database);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        final File space = new File("backup/TMP-F-" + user + "-" + System.currentTimeMillis());

        if (!space.exists())
            space.mkdir();

        databaseService.updateAll(space);

        this.databaseService.loadAll(space);
        this.databaseService.updateAll();
    }

    @Override
    public boolean existsBackup(String name) {
        return new File("backup/" + name).exists();
    }

    @Override
    public List<String> getBackups() throws NullPointerException {
        return Arrays.stream(Objects.requireNonNull(new File("backup").listFiles())).map(File::getName).collect(Collectors.toList());
    }


}
