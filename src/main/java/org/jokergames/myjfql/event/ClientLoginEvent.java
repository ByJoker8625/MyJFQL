package org.jokergames.myjfql.event;

import org.jokergames.myjfql.command.executor.Executor;

/**
 * @author Janick
 */

public class ClientLoginEvent extends Event {

    public static final String TYPE = "ClientLoginEvent";
    private final Executor executor;
    private final boolean successfully;

    public ClientLoginEvent(Executor executor, boolean successfully) {
        super(TYPE);
        this.executor = executor;
        this.successfully = successfully;
    }

    public Executor getExecutor() {
        return executor;
    }

    public boolean isSuccessfully() {
        return successfully;
    }
}
