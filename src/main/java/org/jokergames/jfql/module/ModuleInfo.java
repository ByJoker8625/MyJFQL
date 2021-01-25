package org.jokergames.jfql.module;

import java.io.File;

/**
 * @author Janick
 */

public class ModuleInfo {

    private final File file;

    private final String name;
    private final String main;

    public ModuleInfo(File file, String name, String main) {
        this.file = file;
        this.name = name;
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public String getMain() {
        return main;
    }
}
