package org.jokergames.jfql.user;

import java.util.List;
import java.util.UUID;

/**
 * @author Janick
 */

public class ConsoleUser extends User {

    public ConsoleUser() {
        super("Console", UUID.randomUUID().toString());

        setProperties(List.of(Property.CONSOLE, Property.NO_DELETE, Property.NO_EDIT));
        setPermissions(List.of("*"));
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

}
