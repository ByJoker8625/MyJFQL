package de.byjoker.myjfql.user;

import de.byjoker.myjfql.util.StorageService;

import java.util.List;

public interface UserService extends StorageService {

    void createUser(User user);

    void saveUser(User user);

    User getUserByIdentifier(String identifier);

    User getUserByName(String name);

    User getUser(String id);

    boolean existsUserByIdentifier(String identifier);

    boolean existsUserByName(String name);

    boolean existsUser(String id);

    void deleteUser(String id);

    List<User> getUsers();

}
