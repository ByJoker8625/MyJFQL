package org.jokergames.myjfql.core.boot;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.core.ScriptEditor;

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
                new MyJFQL().start();
            }, BootSection.Type.DEFAULT));
        }

        registry.invoke(args);
    }

}
