package de.byjoker.myjfql.util;

import java.io.File;

public interface StorageService {

    void loadAll();

    void loadAll(File backend);

    void updateAll();

    void updateAll(File backend);

}
