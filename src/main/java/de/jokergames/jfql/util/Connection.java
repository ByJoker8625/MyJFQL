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

    private JSONObject jsonObject;

    public void connect(String url) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();

        int i;

        while ((i = bufferedReader.read()) != -1) {
            stringBuilder.append((char) i);
        }

        this.jsonObject = new JSONObject(stringBuilder.toString());
    }

    public boolean latestIsBeta() {
        return jsonObject.getString("Version").endsWith("-BETA") || jsonObject.getString("Version").endsWith("-SNAPSHOT");
    }

    public boolean isLatest() {
        if (jsonObject == null)
            return true;

        return jsonObject.getString("Version").equals(JFQL.getInstance().getVersion());
    }

    public boolean isMaintenance() {
        if (jsonObject == null)
            return false;

        return jsonObject.getBoolean("Maintenance");
    }

    public String getDownload() {
        if (jsonObject == null)
            return null;

        return jsonObject.getString("Download");
    }

    public JSONObject getResponse() {
        return jsonObject;
    }
}
