package org.jokergames.myjfql.event;

import org.jokergames.myjfql.script.Script;
import org.jokergames.myjfql.user.User;

/**
 * @author Janick
 */

public class InvokeScriptEvent extends Event {

    public static final String TYPE = "InvokeScriptEvent";
    private final User user;
    private final Script script;

    public InvokeScriptEvent(User user, Script script) {
        super(TYPE);
        this.user = user;
        this.script = script;
    }

    public User getUser() {
        return user;
    }

    public Script getScript() {
        return script;
    }
}
