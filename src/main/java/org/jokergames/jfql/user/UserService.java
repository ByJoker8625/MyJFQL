package org.jokergames.jfql.user;

import org.jokergames.jfql.util.FileFactory;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class UserService {

    private final FileFactory fileFactory;

    public UserService(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
    }

    public void saveUser(User user) {
        final File file = new File("user/" + user.getName() + ".json");

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", user.getName());
        jsonObject.put("password", user.getPassword());
        jsonObject.put("permissions", user.getPermissions());
        jsonObject.put("properties", user.getProperties());

        fileFactory.save(file, jsonObject);
    }

    public User getUser(String name) {
        final File file = new File("user/" + name + ".json");

        if (!file.exists()) {
            return null;
        }

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

            return remoteUser;
        } else {
            ConsoleUser consoleUser = new ConsoleUser();
            consoleUser.setProperties(properties);
            consoleUser.setPermissions(permissions);

            return consoleUser;
        }

    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        for (File file : new File("user").listFiles()) {
            User current = getUser(file.getName().replace(".json", ""));

            if (current != null)
                users.add(current);
        }

        return users;
    }

}
