package de.byjoker.myjfql.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Updater {

    private final String version;
    private JSONObject serverConfiguration = null;

    public Updater(String version) {
        this.version = version;
    }

    public void fetch(String url) throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8));
        final StringBuilder builder = new StringBuilder();

        int i;

        while ((i = reader.read()) != -1) {
            builder.append((char) i);
        }

        this.serverConfiguration = new JSONObject(builder.toString());
    }

    public VersionCompatibilityStatus getCompatibilityStatus() {
        final JSONObject compatibility = serverConfiguration.getJSONObject("compatibility");

        if (serverConfiguration.getString("version").equals(version))
            return VersionCompatibilityStatus.SAME;

        for (final String status : compatibility.keySet()) {
            final JSONObject compatibilityStatus = compatibility.getJSONObject(status);

            String newer = null;
            String older = null;

            if (!compatibilityStatus.isNull("newer"))
                newer = compatibilityStatus.getString("newer");

            if (!compatibilityStatus.isNull("older"))
                older = compatibilityStatus.getString("older");

            switch (VersionCompatibilityStatus.valueOf(status)) {
                case JUST_FINE: {
                    if (compareVersion(version, newer))
                        return VersionCompatibilityStatus.JUST_FINE;

                    break;
                }
                case SOME_CHANGES: {
                    if (compareVersion(version, newer) && !compareVersion(version, older))
                        return VersionCompatibilityStatus.SOME_CHANGES;

                    break;
                }
            }
        }

        return VersionCompatibilityStatus.PENSIONER;
    }

    private boolean compareVersion(String versionA, String versionB) {
        return versionA.compareTo(versionB) > 0;
    }

    public String getLatestVersion() {
        return serverConfiguration.getString("version");
    }

    public List<String> getVersions() {
        return new ArrayList<>(getDownloads().keySet());
    }

    public Map<String, String> getDownloads() {
        final Map<String, Object> raw = serverConfiguration.getJSONObject("downloads").toMap();
        return raw.keySet().stream().collect(Collectors.toMap(key -> key, key -> raw.get(key).toString(), (a, b) -> b));
    }

    public JSONObject getServerConfiguration() {
        return serverConfiguration;
    }

    public Downloader getDownloader() {
        return new Downloader(this);
    }

    public enum VersionCompatibilityStatus {
        PENSIONER,
        SOME_CHANGES,
        JUST_FINE,
        SAME
    }

}
