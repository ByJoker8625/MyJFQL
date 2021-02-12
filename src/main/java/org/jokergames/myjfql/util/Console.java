package org.jokergames.myjfql.util;

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

    public void println(String s) {
        System.out.println(s);
    }

    public void print(String s) {
        System.out.print(s);
    }

    public void clean() {
        println("");
    }

    public String read() {
        return scanner.nextLine();
    }

    public void logInfo(String s) {
        println("[" + getTime() + "] INFO: " + s);
    }

    public void logError(String s) {
        println("[" + getTime() + "] ERROR: " + s);
    }

    public void log(String s) {
        println("[" + getTime() + "] " + s);
    }

    public void logWarning(String s) {
        println("[" + getTime() + "] WARNING: " + s);
    }

    public Scanner getScanner() {
        return scanner;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

}
