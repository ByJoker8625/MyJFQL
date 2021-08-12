package org.jokergames.myjfql.user;

import java.util.ArrayList;
import java.util.List;

public class User {

    private final String name;
    private String password;

    private List<String> permissions;
    private boolean staticDatabase;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.permissions = new ArrayList<>();
        this.staticDatabase = false;
    }

    public boolean hasPermission(final String permission) {
        if (!permission.startsWith("-") && permissions.contains("*"))
            return true;

        return permissions.contains(permission.toLowerCase());
    }

    public void addPermission(final String permission) {
        if (!hasPermission(permission)) {
            permissions.add(permission.toLowerCase());
        }
    }

    public void removePermission(final String permission) {
        permissions.remove(permission.toLowerCase());
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(final List<String> permissions) {
        this.permissions = permissions;
    }

    public boolean isStaticDatabase() {
        return staticDatabase;
    }

    public void setStaticDatabase(final boolean staticDatabase) {
        this.staticDatabase = staticDatabase;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", permissions=" + permissions +
                ", staticDatabase=" + staticDatabase +
                '}';
    }
}
