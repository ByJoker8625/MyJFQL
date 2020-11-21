package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.core.lang.VirtualQueryScript;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.user.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class VqsCommand extends Command {

    public VqsCommand() {
        super("VQS", List.of("COMMAND", "FILE", "URL", "LINE", "SHOW"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        VirtualQueryScript virtualQueryScript;

        if (!arguments.containsKey("SHOW")) {
            virtualQueryScript = new VirtualQueryScript(user);
        } else {
            virtualQueryScript = new VirtualQueryScript(user, true);
        }

        if (executor instanceof RemoteExecutor) {
            final RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.vqs")) {
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

                if (!user.hasPermission("execute.vqs.url.*") && !user.hasPermission("execute.vqs.url." + url.toString())) {
                    return false;
                }


                if (!path.toLowerCase().endsWith(".jfql")) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("File '" + path + "' isn't a .jfql file!")));
                    return true;
                }

                List<String> queries = new ArrayList<>();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
                    String read;

                    while ((read = reader.readLine()) != null) {
                        queries.add(read);
                    }

                } catch (Exception ex) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown url error!")));
                    return true;
                }

                if (queries.isEmpty()) {
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

                virtualQueryScript.invokeLineScript(queries, line);
                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                return true;
            }

            if (arguments.containsKey("FILE")) {
                final String path = JFQL.getInstance().getFormatter().formatString(arguments.get("FILE"));
                File file = new File(path);

                if (!file.exists()) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("File '" + path + "' doesn't exists!")));
                    return true;
                }

                if (!user.hasPermission("execute.vqs.file.*") && !user.hasPermission("execute.vqs.file." + file.getName())) {
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

                List<String> queries = new ArrayList<>();

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String read;

                    while ((read = reader.readLine()) != null) {
                        queries.add(read);
                    }

                } catch (Exception ex) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown file error!")));
                    return true;
                }

                if (queries.isEmpty()) {
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

                virtualQueryScript.invokeLineScript(queries, line);
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

                List<String> queries = new ArrayList<>();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
                    String read;

                    while ((read = reader.readLine()) != null) {
                        queries.add(read);
                    }

                } catch (Exception ex) {
                    JFQL.getInstance().getConsole().logError("Unknown url error!");
                    return true;
                }

                if (queries.isEmpty()) {
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

                virtualQueryScript.invokeLineScript(queries, line);
                JFQL.getInstance().getConsole().logInfo("Execute all queries.");
                return true;
            }

            if (arguments.containsKey("FILE")) {
                final String path = JFQL.getInstance().getFormatter().formatString(arguments.get("FILE"));
                File file = new File(path);

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

                List<String> queries = new ArrayList<>();

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String read;

                    while ((read = reader.readLine()) != null) {
                        queries.add(read);
                    }

                } catch (Exception ex) {
                    JFQL.getInstance().getConsole().logError("Unknown file error!");
                    return true;
                }

                if (queries.isEmpty()) {
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

                virtualQueryScript.invokeLineScript(queries, line);
                JFQL.getInstance().getConsole().logInfo("Execute all queries.");
                return true;
            }

            JFQL.getInstance().getConsole().logError("Unknown syntax!");
        }

        return true;
    }
}
