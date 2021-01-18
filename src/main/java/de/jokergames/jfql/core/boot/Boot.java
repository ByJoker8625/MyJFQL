package de.jokergames.jfql.core.boot;

import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.core.ScriptEditor;

import java.util.List;

/**
 * @author Janick
 */

public class Boot {

    public static void main(String[] args) {
        BootRegistry registry = new BootRegistry();

        {
            registry.registerSection(new BootSection("script", List.of(new BootArgument("scripteditor", null)), arguments -> {
                new ScriptEditor().start();
            }));

            registry.registerSection(new BootSection("myjfql", List.of(), arguments -> {
                new JFQL().start();
            }, BootSection.Type.DEFAULT));
        }

        registry.invoke(args);
    }

}
