package de.byjoker.myjfql.util;

import java.io.File;

public interface StorageService {

    void load();

    void load(File space);

    void update();

    void update(File space);

}
