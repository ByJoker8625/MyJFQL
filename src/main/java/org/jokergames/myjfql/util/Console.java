package org.jokergames.myjfql.util;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.FileException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        logIntoFile(s + "\n");
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
        logIntoFile(s + "\n");
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

    public void stc() {
        List<String> nativeCommands = new ArrayList<>();
        List<String> lowerCommands = new ArrayList<>();


        MyJFQL.getInstance().getCommandService().getCommands().forEach(command -> {
            nativeCommands.add(command.getName());
            lowerCommands.add(command.getName().toLowerCase());
            for (String alias : command.getAliases()) {
                nativeCommands.add(alias);
                lowerCommands.add(alias.toLowerCase());
            }
        });

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

    public void printInfo(String s) {
        print("[" + getTime() + "] INFO: " + s);
    }

    public void logError(String s) {
        println("[" + getTime() + "] ERROR: " + s);
    }

    public void printError(String s) {
        print("[" + getTime() + "] ERROR: " + s);
    }

    public void logWarning(String s) {
        println("[" + getTime() + "] WARNING: " + s);
    }

    public void printWarning(String s) {
        print("[" + getTime() + "] WARNING: " + s);
    }

    public void log(String s) {
        println("[" + getTime() + "] " + s);
    }

    public void logServerError(String name, String s) {
        logIntoFile("[" + getTime() + "] ERROR: [SERVER/" + name + "] " + s + "\n");
    }

    public void logServerInfo(String name, String s) {
        logIntoFile("[" + getTime() + "] INFO: [SERVER/" + name + "] " + s + "\n");
    }

    public void logServerWarning(String name, String s) {
        logIntoFile("[" + getTime() + "] WARNING: [SERVER/" + name + "] " + s + "\n");
    }

    private void logIntoFile(String s) {
        try {
            final File file = new File("log.txt");
            String logged = "";

            {
                FileReader reader = new FileReader(file);
                StringBuilder builder = new StringBuilder();

                int i;

                while ((i = reader.read()) != -1) {
                    builder.append((char) i);
                }

                logged = builder.toString();
            }

            logged += s;

            {
                FileWriter writer = new FileWriter(file);
                writer.write(logged);
                writer.close();
            }
        } catch (Exception ex) {
            throw new FileException("Can't access file!");
        }
    }

    public ConsoleReader getReader() {
        return reader;
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
