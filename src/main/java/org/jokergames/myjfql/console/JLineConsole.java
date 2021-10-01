package org.jokergames.myjfql.console;

import jline.console.ConsoleReader;
import jline.console.completer.CandidateListCompletionHandler;
import jline.console.completer.StringsCompleter;
import org.jetbrains.annotations.Nullable;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.ConsoleException;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

public class JLineConsole implements Console {

    private final ConsoleReader reader;
    private final PrintWriter writer;

    private boolean finish;

    public JLineConsole() {
        this.finish = false;

        try {
            this.reader = new ConsoleReader();
            this.writer = new PrintWriter(reader.getOutput());
        } catch (Exception ex) {
            throw new ConsoleException(ex);
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
                JLineConsole.this.print(s);
            }

            @Override
            public void println(final @Nullable String s) {
                JLineConsole.this.println(s);
            }
        });
    }

    @Override
    public void clean() {
        println(null);
    }

    @Override
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
                    throw new ConsoleException(ex);
                }
            }
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
    public void clear() {
        try {
            reader.clearScreen();
        } catch (IOException e) {
            throw new ConsoleException(e);
        }
    }

    @Override
    public String readPrompt() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void complete() {
        reader.addCompleter(new StringsCompleter(MyJFQL.getInstance().getCommandService().getCommands().stream().map(command -> command.getName().toLowerCase()).collect(Collectors.toList())));
        finish = true;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public ConsoleReader getReader() {
        return reader;
    }

    private String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}
