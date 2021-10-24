package de.byjoker.myjfql.config;

import org.json.JSONObject;

public interface Config {

    int port();

    boolean server();

    boolean updates();

    boolean autoUpdate();

    String updateHost();

    String encryption();

    boolean jline();

    boolean showConnections();

    boolean showQueries();

    JSONObject asJson();

}
