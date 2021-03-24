package org.jokergames.myjfql.user;

import java.util.List;

/**
 * @author Janick
 */

public class RemoteUser extends User {

    public RemoteUser(String name, String password) {
        super(name, password);
        setProperties(List.of(Property.STATIC_DATABASE));
    }
}
