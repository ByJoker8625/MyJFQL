package de.byjoker.myjfql.console;

import de.byjoker.myjfql.exception.ConsoleException;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ScannerConsole implements Console {

    private final Scanner reader;
    private final PrintWriter writer;

    public ScannerConsole() {

        try {
            reader = new Scanner(System.in);
            writer = new PrintWriter(System.out);
        } catch (Exception ex) {
            throw new ConsoleException(ex);
        }

        System.setErr(new PrintStream(System.err) {
            @Override
            public void print(final @Nullable String s) {
                printError(s);
            }

            @Override
            public void println(final @Nullable String s) {
                print(s + "\n");
            }
        });

        System.setOut(new PrintStream(System.out) {
            @Override
            public void print(final @Nullable String s) {
                ScannerConsole.this.print(s);
            }

            @Override
            public void println(final @Nullable String s) {
                ScannerConsole.this.println(s);
            }
        });


    }

    @Override
    public void clean() {
        println(null);
    }

    @Override
    public void print(String s) {
        if (s == null) {
            writer.print("");
        } else {
            writer.print("[" + getTime() + "] " + s);
        }

        writer.flush();
    }

    @Override
    public void println(final String s) {
        if (s == null)
            writer.println();
        else
            print(s + "\n");
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
    public void logError(String s) {
        println("ERROR: " + s);
    }

    @Override
    public void logWarning(String s) {
        println("WARNING: " + s);
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

    @Override
    public String readPrompt() {
        return reader.nextLine();
    }

    @Deprecated
    @Override
    public void clear() {
        throw new ConsoleException("This feature is only supported with JLine!");
    }

    @Deprecated
    @Override
    public void complete() {
    }

    private String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public Scanner getReader() {
        return reader;
    }
}
