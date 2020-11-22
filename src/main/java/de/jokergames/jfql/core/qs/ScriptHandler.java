package de.jokergames.jfql.core.qs;

import de.jokergames.jfql.core.qs.rw.ScriptReader;
import de.jokergames.jfql.core.qs.rw.ScriptWriter;
import de.jokergames.jfql.exception.FileException;

import java.io.File;
import java.util.List;

/**
 * @author Janick
 */

public class ScriptHandler {

    public void invokeScript(QueryScript queryScript) {
        new ScriptInvoker(queryScript).invokeScript();
    }

    public void invokeScript(QueryScript queryScript, int j) {
        new ScriptInvoker(queryScript).invokeLineScript(j);
    }

    public void saveQueryScript(String name, QueryScript script) {
        saveQueryScript(name, script.getQueries());
    }

    public void saveQueryScript(String name, List<String> queries) {
        ScriptWriter writer = new ScriptWriter(new File("script/" + name + ".jfql"), queries);

        try {
            writer.writeFile();
        } catch (Exception exception) {
            throw new FileException("Can't write script: " + name);
        }
    }

    public List<String> getQueryScript(String name) {
        final ScriptReader reader = new ScriptReader("script/" + name + ".jfql");

        try {
            return reader.readScript();
        } catch (Exception exception) {
            throw new FileException("Can't load script: " + name);
        }

    }

}
