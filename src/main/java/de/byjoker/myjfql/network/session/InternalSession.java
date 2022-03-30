package de.byjoker.myjfql.network.session;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class InternalSession extends Session {

    public InternalSession(String name) {
        super(name, Type.INTERNAL, name, null, Collections.emptyList());
    }

    @Override
    public boolean validAddress(@NotNull String address) {
        return false;
    }
}
