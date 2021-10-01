package org.jokergames.myjfql.console;

public interface Console {

    void clean();

    void log(String s);

    void logInfo(String s);

    void logWarning(String s);

    void logError(String s);

    void print(String s);

    void println(String s);

    void printWarning(String s);

    void printInfo(String s);

    void printError(String s);

    String readPrompt();

    void clear();

    void complete();

}
