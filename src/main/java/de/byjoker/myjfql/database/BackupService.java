package de.byjoker.myjfql.database;

import java.util.List;

public interface BackupService {

    void createBackup(String name);

    void deleteBackup(String name);

    boolean existsBackup(String name);

    void loadBackup(String name);

    List<String> getBackups();

}
