package de.byjoker.myjfql.util;

import java.io.File;

public interface GarbageCollector<T extends Garbage> {

    void loadAll();

    void loadAll(File space);

    void load(String identifier);

    void unload(T entity);

    void update(T entity);

    void updateAll();

    void updateAll(File space);

    void collectGarbage();

}
