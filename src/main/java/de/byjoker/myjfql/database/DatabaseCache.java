package de.byjoker.myjfql.database;

import java.io.File;

public interface DatabaseCache {

    void push(Database database);

    void pushAll(File directory);

    void drop(String id);

    void dropAll();

}
