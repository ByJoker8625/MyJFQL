package org.jokergames.myjfql.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Janick
 */

public class Database {

    private final String name;
    private List<Table> tables;

    public Database(String name) {
        this.name = name;
        this.tables = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }


    public void addTable(Table table) {
        removeTable(table.getName());
        tables.add(table);
    }

    public void removeTable(String name) {
        tables.removeIf(table -> table.getName().equals(name));
    }


    public File getFile() {
        return new File("database/" + name + ".json");
    }

    public Table getTable(String name) {
        for (Table table : tables) {
            if (table.getName().equals(name)) {
                return table;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Database database = (Database) o;
        return Objects.equals(name, database.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "DataBase{" +
                "name='" + name + '\'' +
                ", tables=" + tables +
                '}';
    }

}

