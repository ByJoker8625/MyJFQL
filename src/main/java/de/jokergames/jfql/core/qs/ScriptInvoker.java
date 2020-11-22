package de.jokergames.jfql.core.qs;

import de.jokergames.jfql.core.JFQL;

import java.util.List;

/**
 * @author Janick
 */

public class ScriptInvoker {

    private final QueryScript queryScript;
    private final List<String> queries;

    public ScriptInvoker(QueryScript queryScript) {
        this.queryScript = queryScript;
        this.queries = queryScript.getQueries();
    }

    public void invokeScript() {
        for (int i = 0; i < queries.size(); i++) {
            invokeLineScript(i);
        }
    }

    public void invokeLineScript(int line) {
        if (line == -1) {
            invokeScript();
            return;
        }

        final String query = queries.get(line);

        try {
            if (queryScript.isVisible()) JFQL.getInstance().getConsole().logInfo("Invoking query [\"" + query + "\"]");
            JFQL.getInstance().getCommandService().execute(queryScript.getUser(), queryScript.getExecutor(), JFQL.getInstance().getFormatter().formatCommand(query));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public QueryScript getQueryScript() {
        return queryScript;
    }
}
