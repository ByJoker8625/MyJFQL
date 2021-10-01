package org.jokergames.myjfql.config;

public interface Config {

    String getUpdateHost();

    int getServerPort();

    boolean enabledServer();

    boolean enabledUpdates();

    boolean updateCheck();

    boolean enabledJLine();

    boolean showConnectionPacket();

}
