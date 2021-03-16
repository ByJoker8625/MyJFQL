package org.jokergames.myjfql.module;

import java.io.File;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleInfo that = (ModuleInfo) o;
        return Objects.equals(name, that.name) ||
                Objects.equals(main, that.main);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, main);
    }

    @Override
    public String toString() {
        return "ModuleInfo{" +
                "file=" + file +
                ", name='" + name + '\'' +
                ", main='" + main + '\'' +
                '}';
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
