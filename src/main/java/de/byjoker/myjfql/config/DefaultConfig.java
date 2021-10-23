package de.byjoker.myjfql.config;

public class DefaultConfig implements Config {

    @Override
    public String getUpdateHost() {
        return "https://joker-games.org/api/myjfql.json";
    }

    @Override
    public int getServerPort() {
        return 2291;
    }

    @Override
    public boolean enabledServer() {
        return true;
    }

    @Override
    public boolean updateCheck() {
        return true;
    }

    @Override
    public boolean showConnectionPacket() {
        return true;
    }

    @Override
    public boolean enabledUpdates() {
        return false;
    }

    @Override
    public boolean enabledJLine() {
        return true;
    }

}
