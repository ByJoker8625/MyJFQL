package de.byjoker.myjfql.console;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemConsole implements Console {

    @Override
    public void clean() {
        println(null);
    }

    @Override
    public void log(String s) {
        println(s);
    }

    @Override
    public void logInfo(String s) {
        println("INFO: " + s);
    }

    @Override
    public void logWarning(String s) {
        println("WARNING: " + s);
    }

    @Override
    public void logError(String s) {
        println("ERROR: " + s);
    }

    @Override
    public void print(String s) {
        if (s == null) {
            System.out.println();
            return;
        }

        System.out.println("[" + getTime() + "] " + s);
    }

    @Override
    public void println(String s) {
        if (s == null) {
            System.out.println();
            return;
        }

        System.out.println("[" + getTime() + "] " + s);
    }

    @Override
    public void printWarning(String s) {
        print("WARNING: " + s);
    }

    @Override
    public void printInfo(String s) {
        print("INFO: " + s);
    }

    @Override
    public void printError(String s) {
        print("ERROR: " + s);
    }


    @Deprecated
    @Override
    public String readPrompt() {
        return null;
    }

    @Deprecated
    @Override
    public void clear() {
    }

    @Deprecated
    @Override
    public void complete() {
    }

    private String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}
