package de.byjoker.myjfql.console;

import de.byjoker.myjfql.exception.ConsoleException;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ConsoleImpl implements Console {

    private Scanner scanner = null;

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

        System.out.println("[" + time() + "] " + s);
    }

    @Override
    public void println(String s) {
        if (s == null) {
            System.out.println();
            return;
        }

        System.out.println("[" + time() + "] " + s);
    }

    @Override
    public String readPrompt() {
        try {
            return scanner.nextLine();
        } catch (Exception ignore) {
        }

        return "";
    }

    @Override
    public void bind(@NotNull Object... parameters) {
        scanner = new Scanner(System.in);
    }

    @Override
    public void clear() {
        throw new ConsoleException("The standard java console doesn't offer any method for clearing the screen!");
    }

    private String time() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}
