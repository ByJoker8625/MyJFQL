package org.jokergames.myjfql.util;

import jline.console.ConsoleReader;
import jline.console.completer.CandidateListCompletionHandler;
import jline.console.completer.StringsCompleter;
import org.jetbrains.annotations.Nullable;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.CommandException;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

public class Console {

    private final ConsoleReader reader;
    private final PrintWriter writer;

    private boolean finish;

    public Console() {
        this.finish = false;

        try {
            this.reader = new ConsoleReader();
            this.writer = new PrintWriter(reader.getOutput());
        } catch (Exception ex) {
            throw new CommandException(ex);
        }

        reader.setPrompt("");
        reader.setExpandEvents(false);

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
                Console.this.print(s);
            }

            @Override
            public void println(final @Nullable String s) {
                Console.this.println(s);
            }
        });
    }

    public void clean() {
        println(null);
    }

    public void print(final String s) {
        if (s == null)
            writer.print("");
        else {
            writer.print("[" + getTime() + "] " + s);

            if (finish) {
                try {
                    String buffer = reader.getCursorBuffer().toString();

                    CandidateListCompletionHandler.setBuffer(reader, buffer, buffer.length());
                } catch (Exception ex) {
                    throw new CommandException(ex);
                }
            }
        }

        writer.flush();
    }

    public void println(final String s) {
        if (s == null)
            writer.println();
        else
            print(s + "\n");
    }

    public void logInfo(String s) {
        println("INFO: " + s);
    }

    public void logError(String s) {
        println("ERROR: " + s);
    }

    public void logWarning(String s) {
        println("WARNING: " + s);
    }

    public void printWarning(String s) {
        print("WARNING: " + s);
    }

    public void printInfo(String s) {
        print("INFO: " + s);
    }

    public void printError(String s) {
        print("ERROR: " + s);
    }

    public void clear() {
        try {
            reader.clearScreen();
        } catch (IOException e) {
            throw new CommandException(e);
        }
    }

    public String readPrompt() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public void complete() {
        reader.addCompleter(new StringsCompleter(MyJFQL.getInstance().getCommandService().getCommands().stream().map(command -> command.getName().toLowerCase()).collect(Collectors.toList())));
        finish = true;
    }

    private String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}
