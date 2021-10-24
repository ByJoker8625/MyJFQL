package de.byjoker.myjfql.user;

import de.byjoker.myjfql.util.StorageService;

import java.util.List;

public interface UserService extends StorageService {

    void createUser(User user);

    void saveUser(User user);

    User getUser(String name);

    boolean existsUser(String name);

    void deleteUser(String name);

    List<User> getUsers();

}
