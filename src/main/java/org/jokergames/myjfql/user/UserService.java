package org.jokergames.myjfql.user;

import org.jokergames.myjfql.util.FileFactory;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class UserService {

    private final FileFactory fileFactory;
    private final List<User> users;

    public UserService(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
        this.users = new ArrayList<>();
    }

    public void saveUser(User user) {
        for (int i = 0, scriptsSize = users.size(); i < scriptsSize; i++) {
            if (users.get(i).getName().equals(user.getName())) {
                users.set(i, user);
                return;
            }
        }

        users.add(user);
    }

    public User getUser(String name) {
        return users.stream().filter(user -> user.getName().equals(name)).findFirst().orElse(null);
    }

    public List<User> getUsers() {
        return users;
    }

    public void init() {
        for (File file : new File("user").listFiles()) {
            final JSONObject jsonObject = fileFactory.load(file);
            List<User.Property> properties = new ArrayList<>();
            List<String> permissions = new ArrayList<>();

            for (Object prop : jsonObject.getJSONArray("properties").toList()) {
                properties.add(User.Property.valueOf(prop.toString()));
            }

            for (Object perms : jsonObject.getJSONArray("permissions").toList()) {
                permissions.add(perms.toString().toLowerCase());
            }

            if (!properties.contains(User.Property.CONSOLE)) {
                RemoteUser remoteUser = new RemoteUser(jsonObject.getString("name"), jsonObject.getString("password"));
                remoteUser.setProperties(properties);
                remoteUser.setPermissions(permissions);

                users.add(remoteUser);
            } else {
                ConsoleUser consoleUser = new ConsoleUser();
                consoleUser.setProperties(properties);
                consoleUser.setPermissions(permissions);

                users.add(consoleUser);
            }
        }

    }

    public void update() {
        for (User user : users) {
            final File file = new File("user/" + user.getName() + ".json");

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", user.getName());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("permissions", user.getPermissions());
            jsonObject.put("properties", user.getProperties());

            fileFactory.save(file, jsonObject);
        }
    }

}
