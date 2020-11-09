package de.jokergames.jfql.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * @author Janick
 */

public class Console {

    private final PrintStream printStream;
    private final Scanner scanner;

    public Console() {
        this.printStream = System.out;
        this.scanner = new Scanner(System.in);
    }

    public void clean(String s) {
        System.out.println(s);
    }

    public void clean() {
        clean("");
    }

    public String read() {
        return scanner.nextLine();
    }

    public void logInfo(String s) {
        clean("[" + getTime() + "] INFO: " + s);
    }

    public void logError(String s) {
        clean("[" + getTime() + "] ERROR: " + s);
    }

    public void logWarning(String s) {
        clean("[" + getTime() + "] WARNING: " + s);
    }

    public Scanner getScanner() {
        return scanner;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

}
