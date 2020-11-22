package de.jokergames.jfql.core.qs;

import de.jokergames.jfql.command.executor.ConsoleExecutor;
import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.user.User;

import java.util.List;

/**
 * @author Janick
 */

public class QueryScript {

    private final User user;
    private final Executor executor;
    private boolean show;
    private boolean visible;

    private List<String> queries;

    public QueryScript(User user, boolean show, boolean visible, List<String> queries) {
        this.user = user;
        this.show = show;
        this.visible = visible;
        this.queries = queries;

        if (show) {
            executor = new ConsoleExecutor();
        } else {
            executor = new RemoteExecutor(user.getName(), null);
        }
    }

    public QueryScript(User user, List<String> queries) {
        this(user, false, true, queries);
    }

    public QueryScript(User user, List<String> queries, boolean show) {
        this(user, show, true, queries);
    }

    public User getUser() {
        return user;
    }

    public Executor getExecutor() {
        return executor;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }

    @Override
    public String toString() {
        return "QueryScript{\n" + queries.toString().replace("[", "").replace("]", "").replace(", ", "\n") + "\n}";
    }
}
