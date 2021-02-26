package org.jokergames.myjfql.util;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;
import org.jokergames.myjfql.command.Command;
import org.jokergames.myjfql.core.MyJFQL;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * @author Janick
 */

public class Console {

    private PrintWriter writer;
    private ConsoleReader reader;
    private boolean input;

    public Console() {
        input = true;

        try {
            this.reader = new ConsoleReader();
            this.writer = new PrintWriter(reader.getOutput());

            reader.setPrompt("> ");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void println(String s) {
        writer.println(s);

        if (!input) {
            try {
                reader.redrawLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        writer.flush();
    }

    public void print(String s) {
        writer.print(s);

        if (!input) {
            try {
                reader.redrawLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        writer.flush();
    }

    public void clean() {
        println("");
    }

    public String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public void clear() {
        try {
            reader.clearScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void completer() {
        List<String> nativeCommands = new ArrayList<>();
        List<String> lowerCommands = new ArrayList<>();


        for (Command command : MyJFQL.getInstance().getCommandService().getCommands()) {
            nativeCommands.add(command.getName());
            lowerCommands.add(command.getName().toLowerCase());

            for (String alias : command.getAliases()) {
                nativeCommands.add(alias);
                lowerCommands.add(alias.toLowerCase());
            }

        }

        if (MyJFQL.getInstance().getConfiguration().getBoolean("Uppercase")) {
            reader.addCompleter(new StringsCompleter(nativeCommands));
            reader.addCompleter(new StringsCompleter(lowerCommands));
        } else {
            reader.addCompleter(new StringsCompleter(lowerCommands));
            reader.addCompleter(new StringsCompleter(nativeCommands));
        }

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

    public ConsoleReader getReader() {
        return reader;
    }

    public Scanner getScanner() {
        return new Scanner(System.in);
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

}
