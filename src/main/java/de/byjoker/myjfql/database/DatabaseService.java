package de.byjoker.myjfql.database;

import de.byjoker.myjfql.util.StorageService;

import java.util.List;

public interface DatabaseService extends StorageService {

    void saveDatabase(Database database);

    boolean existsDatabase(String name);

    void deleteDatabase(String name);

    Database getDatabase(String name);

    List<Database> getDatabases();

}
