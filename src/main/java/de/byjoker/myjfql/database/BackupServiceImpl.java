package de.byjoker.myjfql.database;

import java.io.File;
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
            folder.mkdirs();

        if (!file.exists())
            file.mkdirs();

        databaseService.updateAll(file);
    }

    @Override
    public void deleteBackup(String name) {
        new File("backup/" + name).delete();
    }

    @Override
    public void loadBackup(String name) {
        databaseService.loadAll(new File("backup/" + name));
        databaseService.updateAll();
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
