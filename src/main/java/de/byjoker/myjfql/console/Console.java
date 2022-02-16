package de.byjoker.myjfql.console;

import org.jetbrains.annotations.NotNull;

public interface Console {

    void log(String s);

    void logInfo(String s);

    void logWarning(String s);

    void logError(String s);

    void print(String s);

    void println(String s);

    String readPrompt();

    void bind(@NotNull Object... parameters);

    void clear();

}
