package de.byjoker.myjfql.user;

import de.byjoker.myjfql.util.FileFactory;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserServiceImpl implements UserService {

    private final FileFactory factory;
    private final List<User> users;

    public UserServiceImpl(FileFactory fileFactory) {
        this.users = new ArrayList<>();
        this.factory = fileFactory;
    }

    @Override
    public void createUser(User user) {
        // TODO: 23.10.2021
    }

    @Override
    public void saveUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getName().equals(user.getName())) {
                users.set(i, user);
                return;
            }
        }

        users.add(user);
    }

    @Override
    public User getUser(String name) {
        return users.stream().filter(user -> user.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public boolean existsUser(String name) {
        return users.stream().anyMatch(user -> user.getName().equals(name));
    }

    @Override
    public void deleteUser(String name) {
        users.removeIf(user -> user.getName().equals(name));
        new File("user/" + name + ".json").delete();
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void load() {
        load(new File("user"));
    }

    @Override
    public void load(File space) {
        Arrays.stream(Objects.requireNonNull(space.listFiles())).map(factory::load).forEach(jsonObject -> {
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

    @Override
    public void update() {
        update(new File("user"));
    }

    @Override
    public void update(File space) {
        users.forEach(user -> {
            final File file = new File(space.getPath() + "/" + user.getName() + ".json");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", user.getName());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("permissions", user.getPermissions());
            jsonObject.put("staticDatabase", user.isStaticDatabase());
            factory.save(file, jsonObject);
        });
    }

}
