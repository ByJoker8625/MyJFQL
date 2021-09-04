package org.jokergames.myjfql.user;

import org.jokergames.myjfql.util.FileFactory;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserService {

    private final FileFactory factory;
    private final List<User> users;

    public UserService(final FileFactory fileFactory) {
        this.users = new ArrayList<>();
        this.factory = fileFactory;
    }

    public void saveUser(final User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getName().equals(user.getName())) {
                users.set(i, user);
                return;
            }
        }

        users.add(user);
    }

    public User getUser(final String name) {
        return users.stream().filter(user -> user.getName().equals(name)).findFirst().orElse(null);
    }

    public boolean isCreated(final String name) {
        return users.stream().anyMatch(user -> user.getName().equals(name));
    }

    public void deleteUser(final String name) {
        users.removeIf(user -> user.getName().equals(name));
    }

    public List<User> getUsers() {
        return users;
    }

    public void load() {
        Arrays.stream(Objects.requireNonNull(new File("user").listFiles())).map(factory::load).forEach(jsonObject -> {
            List<String> tables = new ArrayList<>();

            if (!jsonObject.isNull("permissions"))
                for (final Object obj : jsonObject.getJSONArray("permissions")) {
                    tables.add(obj.toString());
                }

            final User user = new User(jsonObject.getString("name"), jsonObject.getString("password"));
            user.setStaticDatabase(jsonObject.getBoolean("staticDatabase"));
            user.setPermissions(tables);

            users.add(user);
        });
    }

    public void update() {
        //noinspection ResultOfMethodCallIgnored
        Arrays.stream(Objects.requireNonNull(new File("user").listFiles())).forEach(File::delete);

        users.forEach(user -> {
            final File file = new File("user/" + user.getName() + ".json");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", user.getName());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("permissions", user.getPermissions());
            jsonObject.put("staticDatabase", user.isStaticDatabase());
            factory.save(file, jsonObject);
        });
    }

}
