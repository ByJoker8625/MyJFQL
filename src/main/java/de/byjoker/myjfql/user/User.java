package de.byjoker.myjfql.user;

import de.byjoker.jfql.util.ID;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.DatabaseAction;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String id;
    private String name;
    private String password;

    private Map<String, DatabaseAction> accesses;
    private String preferredDatabase;

    public User(String name, String password) {
        this.id = ID.generateNumber().toString();
        this.name = name;
        this.password = password;
        this.accesses = new HashMap<>();
        this.preferredDatabase = null;
    }

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.accesses = new HashMap<>();
        this.preferredDatabase = null;
    }

    public boolean allowed(String database, DatabaseAction action) {
        return (accesses.containsKey("*") && accesses.get("*").can(action)) || (accesses.containsKey(database) && accesses.get(database).can(action));
    }

    public void grantAccess(String database, DatabaseAction action) {
        accesses.put(database, action);
    }

    public void revokeAccess(String database) {
        accesses.remove(database);
    }

    public Map<String, DatabaseAction> getAccesses() {
        return accesses;
    }

    public void setAccesses(Map<String, DatabaseAction> accesses) {
        this.accesses = accesses;
    }

    public boolean hasPreferredDatabase() {
        return preferredDatabase != null;
    }

    public String getPreferredDatabase() {
        return preferredDatabase;
    }

    public void setPreferredDatabase(String preferredDatabase) {
        this.preferredDatabase = preferredDatabase;
    }

    public boolean validPassword(String password) {
        return this.password.equals(MyJFQL.getInstance().getEncryptor().encrypt(password));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", accesses=" + accesses +
                ", preferredDatabase=" + preferredDatabase +
                '}';
    }

}
