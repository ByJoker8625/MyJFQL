package de.jokergames.jfql.util;

import de.jokergames.jfql.core.JFQL;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author Janick
 */

public class Connection {

    private JSONObject response;

    public void connect(String url) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();

        int i;

        while ((i = bufferedReader.read()) != -1) {
            stringBuilder.append((char) i);
        }

        this.response = new JSONObject(stringBuilder.toString());
    }

    public boolean isLatest() {
        if (response == null)
            return true;

        return response.getString("Version").equals(JFQL.getInstance().getVersion());
    }

    public boolean isMaintenance() {
        if (response == null)
            return false;

        return response.getBoolean("Maintenance");
    }

    public String getDownload() {
        if (response == null)
            return null;

        return response.getString("Download");
    }

    public JSONObject getResponse() {
        return response;
    }
}
