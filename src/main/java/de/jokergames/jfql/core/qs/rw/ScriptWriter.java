package de.jokergames.jfql.core.qs.rw;

import de.jokergames.jfql.core.qs.QueryScript;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class ScriptWriter {

    private final File file;
    private List<String> queries;

    public ScriptWriter(File file) {
        this.file = file;
        this.queries = new ArrayList<>();
    }

    public ScriptWriter(File file, QueryScript queryScript) {
        this.file = file;
        this.queries = queryScript.getQueries();
    }

    public ScriptWriter(File file, List<String> queries) {
        this.file = file;
        this.queries = queries;
    }

    public void writeFile() throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (String query : queries) {
            writer.write(query + "\n");
        }

        writer.close();
    }

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }

    public File getFile() {
        return file;
    }
}

