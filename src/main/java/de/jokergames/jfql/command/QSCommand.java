package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.core.qs.QueryScript;
import de.jokergames.jfql.core.qs.ScriptInvoker;
import de.jokergames.jfql.core.qs.rw.ScriptReader;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.user.User;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class QSCommand extends Command {

    public QSCommand() {
        super("QS", List.of("COMMAND", "FILE", "URL", "LINE", "SHOW"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        QueryScript queryScript;

        if (!arguments.containsKey("SHOW")) {
            queryScript = new QueryScript(user, null);
        } else {
            queryScript = new QueryScript(user, null, false);
        }

        if (executor instanceof RemoteExecutor) {
            final RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.qs")) {
                return false;
            }

            if (arguments.containsKey("URL")) {
                final String path = JFQL.getInstance().getFormatter().formatString(arguments.get("URL"));

                URL url = null;

                try {
                    url = new URL(path);
                } catch (Exception ex) {
                }


                if (url == null) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Url '" + path + "' doesn't exists!")));
                    return true;
                }

                if (!user.hasPermission("execute.qs.url.*") && !user.hasPermission("execute.qs.url." + url.toString())) {
                    return false;
                }


                if (!path.toLowerCase().endsWith(".jfql")) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("File '" + path + "' isn't a .jfql file!")));
                    return true;
                }

                List<String> queries;

                try {
                    queries = new ScriptReader(path).readScript();
                } catch (Exception ex) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown url error!")));
                    return true;
                }

                if (queries == null || queries.isEmpty()) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Queries are empty!")));
                    return true;
                }

                int line = -1;

                if (arguments.containsKey("LINE")) {
                    line = JFQL.getInstance().getFormatter().formatInteger(arguments.get("LINE"));

                    if (queries.get(line) == null) {
                        remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("No query ad line " + line + "!")));
                        return true;
                    }
                }

                queryScript.setQueries(queries);
                new ScriptInvoker(queryScript).invokeLineScript(line);
                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                return true;
            }

            if (arguments.containsKey("FILE")) {
                final String path = JFQL.getInstance().getFormatter().formatString(arguments.get("FILE"));
                File file = new File("script/" + path);

                if (!file.exists()) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("File '" + path + "' doesn't exists!")));
                    return true;
                }

                if (!user.hasPermission("execute.qs.file.*") && !user.hasPermission("execute.qs.file." + file.getName())) {
                    return false;
                }

                if (!file.canRead()) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("File '" + path + "' can't be read!")));
                    return true;
                }

                if (!path.toLowerCase().endsWith(".jfql")) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("File '" + path + "' isn't a .jfql file!")));
                    return true;
                }

                List<String> queries;

                try {
                    queries = new ScriptReader(path).readScript();
                } catch (Exception ex) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown file error!")));
                    return true;
                }

                if (queries == null || queries.isEmpty()) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Queries are empty!")));
                    return true;
                }

                int line = -1;

                if (arguments.containsKey("LINE")) {
                    line = JFQL.getInstance().getFormatter().formatInteger(arguments.get("LINE"));

                    if (queries.get(line) == null) {
                        remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("No query ad line " + line + "!")));
                        return true;
                    }
                }

                queryScript.setQueries(queries);
                new ScriptInvoker(queryScript).invokeLineScript(line);
                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                return true;
            }

            remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSyntax());
            return true;
        } else {

            if (arguments.containsKey("URL")) {
                final String path = JFQL.getInstance().getFormatter().formatString(arguments.get("URL"));

                URL url = null;

                try {
                    url = new URL(path);
                } catch (Exception ex) {
                }


                if (url == null) {
                    JFQL.getInstance().getConsole().logError("Url '" + url + "' doesn't exists!");
                    return true;
                }

                if (!path.endsWith(".jfql")) {
                    JFQL.getInstance().getConsole().logError("Url '" + url + "' isn't a .jfql file!");
                    return true;
                }

                List<String> queries;

                try {
                    queries = new ScriptReader(path).readScript();
                } catch (Exception ex) {
                    JFQL.getInstance().getConsole().logError("Unknown url error!");
                    return true;
                }

                if (queries == null || queries.isEmpty()) {
                    JFQL.getInstance().getConsole().logError("Empty query!");
                    return true;
                }

                int line = -1;

                if (arguments.containsKey("LINE")) {
                    line = JFQL.getInstance().getFormatter().formatInteger(arguments.get("LINE"));

                    if (queries.get(line) == null) {
                        JFQL.getInstance().getConsole().logError("No query ad line " + line + "!");
                        return true;
                    }
                }

                queryScript.setQueries(queries);
                new ScriptInvoker(queryScript).invokeLineScript(line);
                JFQL.getInstance().getConsole().logInfo("Execute all queries.");
                return true;
            }

            if (arguments.containsKey("FILE")) {
                final String path = JFQL.getInstance().getFormatter().formatString(arguments.get("FILE"));
                File file = new File("script/" + path);

                if (!file.exists()) {
                    JFQL.getInstance().getConsole().logError("File '" + path + "' doesn't exists!");
                    return true;
                }

                if (!file.canRead()) {
                    JFQL.getInstance().getConsole().logError("File '" + path + "' can't be read!");
                    return true;
                }

                if (!path.endsWith(".jfql")) {
                    JFQL.getInstance().getConsole().logError("File '" + path + "' isn't a .jfql file!");
                    return true;
                }

                List<String> queries;

                try {
                    queries = new ScriptReader(path).readScript();
                } catch (Exception ex) {
                    JFQL.getInstance().getConsole().logError("Unknown file error!");
                    return true;
                }

                if (queries == null || queries.isEmpty()) {
                    JFQL.getInstance().getConsole().logError("Empty query!");
                    return true;
                }

                int line = -1;

                if (arguments.containsKey("LINE")) {
                    line = JFQL.getInstance().getFormatter().formatInteger(arguments.get("LINE"));

                    if (queries.get(line) == null) {
                        JFQL.getInstance().getConsole().logError("No query ad line " + line + "!");
                        return true;
                    }
                }

                queryScript.setQueries(queries);
                new ScriptInvoker(queryScript).invokeLineScript(line);
                JFQL.getInstance().getConsole().logInfo("Execute all queries.");
                return true;
            }

            JFQL.getInstance().getConsole().logError("Unknown syntax!");
        }

        return true;
    }
}
