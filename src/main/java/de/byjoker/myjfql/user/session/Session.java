package de.byjoker.myjfql.user.session;

import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.UserService;

public class Session {

    private String token;
    private String userId;
    private String databaseId;
    private String address;

    public Session(String token, String userId, String databaseId, String address) {
        this.token = token;
        this.userId = userId;
        this.databaseId = databaseId;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Database getDatabase(DatabaseService service) {
        return service.getDatabase(databaseId);
    }

    public User getUser(UserService service) {
        return service.getUser(userId);
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }
}
