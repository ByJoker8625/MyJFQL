package de.byjoker.myjfql.util;

import java.io.File;

public interface StorageService {

    void loadAll();

    void loadAll(File space);

    void updateAll();

    void updateAll(File space);

}
