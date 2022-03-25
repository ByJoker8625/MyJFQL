package de.byjoker.myjfql.database;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.exception.UserException;
import de.byjoker.myjfql.user.SimpleUser;
import de.byjoker.myjfql.user.UserService;
import de.byjoker.myjfql.util.Json;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class UserTable extends InternalTable {

    private final UserService userService;

    public UserTable(UserService userService) {
        super("users", Arrays.asList("id", "name", "password", "accesses", "preferred_database_id"), "id");
        this.userService = userService;
    }

    @Override
    public void addEntry(TableEntry entry) {
        if (!(entry instanceof RelationalTableEntry)) {
            return;
        }

        if (!entry.contains("id") || !entry.contains("name") || !entry.contains("password") || !entry.contains("accesses")) {
            throw new UserException("Cannot parse table-entry into user!");
        }

        final JSONObject jsonObject = new JSONObject(entry.selectStringify("accesses"));
        final Map<String, DatabasePermissionLevel> accesses = jsonObject.keySet().stream().collect(Collectors.toMap(key -> key, key -> DatabasePermissionLevel.valueOf(jsonObject.getString(key)), (a, b) -> b));

        userService.saveUser(new SimpleUser(entry.selectStringify("id"), entry.selectStringify("name"), MyJFQL.getInstance().getEncryptor().encrypt(entry.selectStringify("password")), accesses, entry.contains("preferred_database_id") ? entry.selectStringify("preferred_database_id") : null));
    }

    @Override
    public void removeEntry(String identifier) {
        userService.deleteUser(identifier);
    }

    @Override
    public Collection<TableEntry> getEntries() {
        return userService.getUsers().stream().map(user -> new RelationalTableEntry().append("id", user.getId()).append("name", user.getName()).append("password", user.getPassword()).append("accesses", Json.stringify(user.getAccesses())).append("preferred_database_id", String.valueOf(user.getPreferredDatabaseId()))).collect(Collectors.toList());
    }

    @Override
    public void clear() {
        getEntries().forEach(entry -> userService.deleteUser(entry.selectStringify("id")));
    }

    public UserService getUserService() {
        return userService;
    }
}
