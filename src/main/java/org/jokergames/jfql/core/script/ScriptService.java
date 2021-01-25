package org.jokergames.jfql.core.script;

import org.jokergames.jfql.command.executor.ConsoleExecutor;
import org.jokergames.jfql.command.executor.Executor;
import org.jokergames.jfql.command.executor.RemoteExecutor;
import org.jokergames.jfql.core.JFQL;
import org.jokergames.jfql.event.InvokeScriptEvent;
import org.jokergames.jfql.exception.FileException;
import org.jokergames.jfql.user.User;
import org.jokergames.jfql.util.FileFactory;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Janick
 */

public class ScriptService {

    private final FileFactory fileFactory;

    public ScriptService(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
    }

    public void invokeScript(String name, User user) {
        invokeScript(name, user, true);
    }

    public void invokeScript(String name, User user, boolean visible) {
        final Script script = getScript(name);

        if (script == null)
            throw new FileException("Script " + name + " doesn't exists!");

        Executor executor;

        if (visible)
            executor = new ConsoleExecutor();
        else
            executor = new RemoteExecutor(UUID.randomUUID().toString(), null);

        List<String> commands = new ArrayList<>();

        for (String command : script.getCommands())
            if (!command.startsWith("#") && !command.startsWith("//")) commands.add(command);

        JFQL.getInstance().getEventService().callEvent(InvokeScriptEvent.TYPE, new InvokeScriptEvent(user, script));

        for (String command : commands)
            JFQL.getInstance().getCommandService().execute(user, executor, JFQL.getInstance().getFormatter().formatCommand(command));
    }

    public void saveScript(Script script) {
        final File file = new File("script/" + script.getName() + ".json");

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", script.getName());
        jsonObject.put("commands", script.getCommands());

        fileFactory.save(file, jsonObject);
    }

    public Script getScript(String name) {
        final File file = new File("script/" + name + ".json");

        if (!file.exists()) {
            return null;
        }

        final JSONObject jsonObject = fileFactory.load(file);

        Script script = new Script(jsonObject.getString("name"));
        List<String> commands = new ArrayList<>();

        for (Object o : jsonObject.getJSONArray("commands").toList()) {
            commands.add(o.toString());
        }

        script.setCommands(commands);


        return script;
    }

    public List<Script> getScripts() {
        List<Script> scripts = new ArrayList<>();

        for (File file : new File("script").listFiles()) {
            Script current = getScript(file.getName().replace(".json", ""));

            if (current != null)
                scripts.add(current);
        }

        return scripts;
    }

}
