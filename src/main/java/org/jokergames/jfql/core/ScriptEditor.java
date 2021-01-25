package org.jokergames.jfql.core;

import org.jokergames.jfql.core.script.Script;
import org.jokergames.jfql.core.script.ScriptService;
import org.jokergames.jfql.util.Console;
import org.jokergames.jfql.util.FileFactory;

import java.util.Scanner;

public final class ScriptEditor {

    private static ScriptEditor instance;
    private final Console console;
    private final ScriptService scriptService;

    public ScriptEditor() {
        instance = this;

        this.console = new Console();
        this.scriptService = new ScriptService(new FileFactory());
    }

    public static ScriptEditor getInstance() {
        return instance;
    }

    public void start() {
        console.print("[" + console.getTime() + "] Script: ");
        String name = console.read();

        if (name.equals("")) {
            console.logError("Bad script name!");
            return;
        }

        if (scriptService.getScript(name) != null) {
            console.print("[" + console.getTime() + "] WARNING: Script already exists. Do you want to overwrite it [y/n]: ");

            if (!console.read().equals("y")) {
                shutdown();
                return;
            }
        }

        final Script script = new Script(name);
        final StringBuilder builder = new StringBuilder();

        console.log("Script: \"" + name + "\" {");
        console.print(": ");

        final Scanner scanner = console.getScanner();
        {
            String scanned;

            while (!(scanned = scanner.nextLine()).equals("}")) {
                if (!scanned.endsWith(";")) {
                    scanned += ";";
                }

                builder.append(scanned);
                console.print(": ");
            }
        }
        script.formatCommands(builder.toString());

        console.logInfo("Script '" + name + "' was saved.");
        scriptService.saveScript(script);
        shutdown();
    }

    public void shutdown() {
        console.logInfo("Closing script editor.");
        System.exit(0);
    }

    public ScriptService getScriptService() {
        return scriptService;
    }

    public Console getConsole() {
        return console;
    }
}
