package de.byjoker.myjfql.user;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.DatabaseActionPerformType;
import de.byjoker.myjfql.util.IDGenerator;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String id;
    private String name;
    private String password;

    private Map<String, DatabaseActionPerformType> accesses;
    private String preferredDatabase;

    public User(String name, String password) {
        this.id = IDGenerator.generateDigits(8);
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

    public boolean allowed(String database, DatabaseActionPerformType action) {
        return (accesses.containsKey("*") && accesses.get("*").can(action)) || (accesses.containsKey(database) && accesses.get(database).can(action));
    }

    public void grantAccess(String database, DatabaseActionPerformType action) {
        accesses.put(database, action);
    }

    public void revokeAccess(String database) {
        accesses.remove(database);
    }

    public Map<String, DatabaseActionPerformType> getAccesses() {
        return accesses;
    }

    public void setAccesses(Map<String, DatabaseActionPerformType> accesses) {
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
        if (password.length() < 8) {
            return false;
        }

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

}
