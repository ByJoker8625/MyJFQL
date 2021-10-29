package de.byjoker.myjfql.user;

import de.byjoker.jfql.util.ID;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.exception.FileException;
import de.byjoker.myjfql.util.FileFactory;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final FileFactory factory;
    private final List<User> users;

    public UserServiceImpl() {
        this.users = new ArrayList<>();
        this.factory = new FileFactory();
    }

    @Override
    public void createUser(User user) {
        if (getUserByName(user.getName()) != null)
            throw new FileException("User already exists!");

        if (existsUser(user.getId())) {
            user.setId(ID.generateNumber().toString());
            createUser(user);
            return;
        }

        user.setPassword(MyJFQL.getInstance().getEncryptor().encrypt(user.getPassword()));
        saveUser(user);
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
    public User getUserByIdentifier(String identifier) {
        if (identifier.startsWith("#"))
            return getUser(identifier.replaceFirst("#", ""));

        return getUserByName(identifier);
    }

    @Override
    public User getUserByName(String name) {
        return users.stream().filter(user -> user.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public User getUser(String id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public boolean existsUserByIdentifier(String identifier) {
        if (identifier.startsWith("#"))
            return existsUser(identifier.replaceFirst("#", ""));

        return existsUserByName(identifier);
    }

    @Override
    public boolean existsUserByName(String name) {
        return users.stream().anyMatch(user -> user.getName().equals(name));
    }

    @Override
    public boolean existsUser(String id) {
        return users.stream().anyMatch(user -> user.getId().equals(id));
    }

    @Override
    public void deleteUser(String id) {
        users.removeIf(user -> user.getName().equals(id));
        new File("user/" + id + ".json").delete();
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void loadAll() {
        loadAll(new File("user"));
    }

    @Override
    public void loadAll(File space) {
        Arrays.stream(Objects.requireNonNull(space.listFiles())).forEach(file -> {
            final JSONObject jsonUser = factory.load(file);
            final JSONObject jsonAccesses = jsonUser.getJSONObject("accesses");

            final String name = file.getName().replaceFirst(".json", "");

            if (name.contains("%") || name.contains("#") || name.contains("'")) {
                MyJFQL.getInstance().getConsole().logWarning("Database used unauthorized characters in the id!");
            } else {
                final User user = new User(name, jsonUser.getString("name"), jsonUser.getString("password"));
                user.setPreferredDatabase(jsonUser.getString("preferred"));
                user.setAccesses(jsonAccesses.keySet().stream().collect(Collectors.toMap(key -> key, key -> DatabaseAction.valueOf(jsonAccesses.getString(key)), (a, b) -> b)));

                if (!user.getName().contains("%") && !user.getName().contains("#") && !user.getName().contains("'"))
                    users.add(user);
                else
                    MyJFQL.getInstance().getConsole().logWarning("User used unauthorized characters in the name!");
            }
        });
    }

    @Override
    public void updateAll() {
        updateAll(new File("user"));
    }

    @Override
    public void updateAll(File space) {
        users.forEach(user -> {
            final File file = new File(space.getPath() + "/" + user.getId() + ".json");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", user.getName());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("accesses", user.getAccesses());
            jsonObject.put("preferred", user.getPreferredDatabase());
            factory.save(file, jsonObject);
        });
    }

}
