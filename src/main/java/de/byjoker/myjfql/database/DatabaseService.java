package de.byjoker.myjfql.database;

import de.byjoker.myjfql.util.StorageService;

import java.io.File;
import java.util.Collection;

public interface DatabaseService extends StorageService {

    void createDatabase(Database database);

    void saveDatabase(Database database);

    boolean existsDatabaseByIdentifier(String identifier);

    boolean existsDatabaseByName(String name);

    boolean existsDatabase(String id);

    void deleteDatabase(String id);

    Database loadDatabase(DatabaseType type, File file);

    Database getDatabaseByIdentifier(String identifier);

    Database getDatabaseByName(String name);

    Database getDatabase(String id);

    Collection<Database> getDatabases();

}
