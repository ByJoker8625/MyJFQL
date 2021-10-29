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
    private long open;
    private long expire;

    public Session(String token, String userId, String databaseId, String address, long open, long expire) {
        this.token = token;
        this.userId = userId;
        this.databaseId = databaseId;
        this.address = address;
        this.open = open;
        this.expire = expire;
    }

    public Session(String token, String userId, String databaseId, String address) {
        this.token = token;
        this.userId = userId;
        this.databaseId = databaseId;
        this.address = address;
        this.open = System.currentTimeMillis();
        this.expire = open + 60000 * 15;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public long getOpen() {
        return open;
    }

    public void setOpen(long open) {
        this.open = open;
    }

    public boolean isExpired() {
        if (expire == -1)
            return false;

        return expire <= System.currentTimeMillis();
    }

    public void utilize() {
        expire = System.currentTimeMillis() + 60000 * 15;
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
