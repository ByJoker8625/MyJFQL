package de.byjoker.myjfql.core;

import de.byjoker.myjfql.command.Command;
import de.byjoker.myjfql.command.CommandSender;
import de.byjoker.myjfql.command.CommandService;
import de.byjoker.myjfql.command.CommandServiceImpl;
import de.byjoker.myjfql.database.DatabasePermissionLevel;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.lang.Interpreter;
import de.byjoker.myjfql.lang.JFQLInterpreter;
import de.byjoker.myjfql.network.NetworkService;
import de.byjoker.myjfql.network.session.InternalSession;
import de.byjoker.myjfql.network.session.Session;
import de.byjoker.myjfql.network.session.SessionService;
import de.byjoker.myjfql.user.UserService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class MyJFQL {

    final Logger logger = LoggerFactory.getLogger(MyJFQL.class);

    private static MyJFQL instance = null;
    private CommandService commandService;
    private NetworkService networkService;
    private SessionService sessionService;
    private DatabaseService databaseService;
    private UserService userService;
    private Interpreter interpreter;

    private MyJFQL() {
        instance = this;

        commandService = new CommandServiceImpl();
        interpreter = new JFQLInterpreter(commandService);
    }

    public static void main(String[] args) {
        MyJFQL.getInstance().start();
    }

    public static MyJFQL getInstance() {
        if (instance == null) {
            return new MyJFQL();
        }

        return instance;
    }

    public void start() {
        commandService.registerCommand(new Command("push", Arrays.asList("command", "fields"), Collections.emptyList()) {
            @Override
            public void execute(@NotNull CommandSender sender, @NotNull Map<String, ? extends List<String>> args) {
                logger.debug(args.toString());

                if (args.containsKey("fields")) {
                    logger.debug(interpreter.interpretPushFieldDefinitions(Objects.requireNonNull(formatString(args.get("fields")))).toString());
                }
            }
        });

        while (true) {
            commandService.execute(new CommandSender() {
                @NotNull
                @Override
                public String getName() {
                    return "null";
                }

                @NotNull
                @Override
                public Session getSession() {
                    return new InternalSession("d");
                }

                @Override
                public boolean permitted(@NotNull DatabasePermissionLevel action, @NotNull String databaseId) {
                    return false;
                }

                @Override
                public void success() {

                }

                @Override
                public void result(@NotNull Object result) {

                }

                @Override
                public void error(@NotNull String exception) {

                }
            }, new Scanner(System.in).nextLine(), interpreter);
        }
    }

    public void shutdown() {

    }

    public CommandService getCommandService() {
        return commandService;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public UserService getUserService() {
        return userService;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }
}
