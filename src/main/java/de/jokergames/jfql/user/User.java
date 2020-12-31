package de.jokergames.jfql.user;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public abstract class User {

    private final String name;
    private List<String> permissions;
    private List<Property> properties;
    private String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.permissions = new ArrayList<>();
        this.properties = new ArrayList<>();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public boolean is(Property property) {
        return properties.contains(property);
    }

    public boolean hasPermission(String permission) {
        if (permissions.contains("*")) {
            return true;
        }

        return permissions.contains(permission.toLowerCase());
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public File getFile() {
        return new File("user/" + name + ".json");
    }

    @Override
    public String toString() {
        return "User{" +
                "permissions=" + permissions +
                ", properties=" + properties +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public enum Property {
        CONSOLE,
        NO_DELETE,
        NO_EDIT
    }
}
