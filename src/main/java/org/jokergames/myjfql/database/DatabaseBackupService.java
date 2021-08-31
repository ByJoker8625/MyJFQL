package org.jokergames.myjfql.database;

import org.apache.commons.io.FileUtils;
import org.jokergames.myjfql.util.FileFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseBackupService {

    private final FileFactory fileFactory;
    private final DatabaseService databaseService;

    public DatabaseBackupService(FileFactory fileFactory, DatabaseService databaseService) {
        this.fileFactory = fileFactory;
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

    public boolean isCreated(final String name) {
        return new File("backup/" + name).exists();
    }

    public List<String> getBackups() throws NullPointerException {
        return Arrays.asList(new File("backup").listFiles()).stream().map(File::getName).collect(Collectors.toList());
    }


}
