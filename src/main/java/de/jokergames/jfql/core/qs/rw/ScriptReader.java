package de.jokergames.jfql.core.qs.rw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class ScriptReader {

    private final String path;

    public ScriptReader(String path) {
        this.path = path;
    }

    public List<String> readScript() throws Exception {
        List<String> queries = null;

        if (path.startsWith("http://") || path.startsWith("https://")) {
            queries = readURL(new URL(path));
        } else {
            queries = readFile(new File(path));
        }

        return queries;
    }

    public List<String> readFile(File file) throws Exception {
        List<String> queries = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String read;

        while ((read = reader.readLine()) != null) {
            queries.add(read);
        }

        reader.close();
        return queries;
    }


    public List<String> readURL(URL url) throws Exception {
        List<String> queries = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
        String read;

        while ((read = reader.readLine()) != null) {
            queries.add(read);
        }

        reader.close();
        return queries;
    }


    public String getPath() {
        return path;
    }
}
