package org.jokergames.myjfql.script;

import org.jokergames.myjfql.command.executor.ConsoleExecutor;
import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.event.InvokeScriptEvent;
import org.jokergames.myjfql.exception.FileException;
import org.jokergames.myjfql.user.User;
import org.jokergames.myjfql.util.FileFactory;
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
    private final List<Script> scripts;

    public ScriptService(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
        this.scripts = new ArrayList<>();
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

        MyJFQL.getInstance().getEventService().callEvent(InvokeScriptEvent.TYPE, new InvokeScriptEvent(user, script));

        for (String command : commands)
            MyJFQL.getInstance().getCommandService().execute(user, executor, MyJFQL.getInstance().getFormatter().formatCommand(command));
    }

    public void deleteScript(String name) {
        deleteScript(new Script(name));
    }

    public void deleteScript(Script script) {
        scripts.remove(script);
    }

    public void saveScript(Script script) {
        for (int i = 0, scriptsSize = scripts.size(); i < scriptsSize; i++) {
            if (scripts.get(i).getName().equals(script.getName())) {
                scripts.set(i, script);
                return;
            }
        }

        scripts.add(script);
    }

    public Script getScript(String name) {
        return scripts.stream().filter(script -> script.getName().equals(name)).findFirst().orElse(null);
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void init() {
        for (File file : new File("script").listFiles()) {
            final JSONObject jsonObject = fileFactory.load(file);

            Script script = new Script(jsonObject.getString("name"));
            List<String> commands = new ArrayList<>();

            for (Object o : jsonObject.getJSONArray("commands").toList()) {
                commands.add(o.toString());
            }

            script.setCommands(commands);
            scripts.add(script);
        }
    }

    public void update() {
        for (Script script : scripts) {
            final File file = new File("script/" + script.getName() + ".json");

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", script.getName());
            jsonObject.put("commands", script.getCommands());

            fileFactory.save(file, jsonObject);
        }
    }

}
