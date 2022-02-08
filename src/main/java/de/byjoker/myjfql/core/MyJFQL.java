package de.byjoker.myjfql.core;

import de.byjoker.myjfql.command.CommandService;
import de.byjoker.myjfql.command.CommandServiceImpl;
import de.byjoker.myjfql.command.ConsoleCommandSender;
import de.byjoker.myjfql.config.Config;
import de.byjoker.myjfql.config.ConfigDefaults;
import de.byjoker.myjfql.config.ConfigService;
import de.byjoker.myjfql.config.ConfigServiceImpl;
import de.byjoker.myjfql.console.Console;
import de.byjoker.myjfql.console.ConsoleCommandCompleter;
import de.byjoker.myjfql.console.ConsoleImpl;
import de.byjoker.myjfql.console.SimpleConsole;
import de.byjoker.myjfql.database.*;
import de.byjoker.myjfql.exception.FileException;
import de.byjoker.myjfql.exception.NetworkException;
import de.byjoker.myjfql.lang.Interpreter;
import de.byjoker.myjfql.lang.JFQLInterpreter;
import de.byjoker.myjfql.server.Server;
import de.byjoker.myjfql.server.session.Session;
import de.byjoker.myjfql.server.session.SessionService;
import de.byjoker.myjfql.server.session.SessionServiceImpl;
import de.byjoker.myjfql.user.UserService;
import de.byjoker.myjfql.user.UserServiceImpl;
import de.byjoker.myjfql.util.*;

import java.util.Timer;
import java.util.TimerTask;

public final class MyJFQL {

    private static MyJFQL instance;

    private final String version = "1.5.5";
    private final Interpreter interpreter;
    private final CommandService commandService;
    private final DatabaseService databaseService;
    private final ConfigService configService;
    private final BackupService databaseBackupService;
    private final UserService userService;
    private final SessionService sessionService;
    private final ConsoleCommandSender consoleCommandSender;
    private final Server server;
    private final Updater updater;
    private final Downloader downloader;
    private Config config;
    private Console console;
    private Encryptor encryptor;

    public MyJFQL() {
        instance = this;
        this.configService = new ConfigServiceImpl();
        this.console = new ConsoleImpl();
        this.config = new ConfigDefaults();
        this.encryptor = new NoneEncryptor();
        this.interpreter = new JFQLInterpreter();
        this.sessionService = new SessionServiceImpl();
        this.consoleCommandSender = new ConsoleCommandSender();
        this.updater = new Updater(version);
        this.commandService = new CommandServiceImpl(interpreter);
        this.userService = new UserServiceImpl();
        this.downloader = updater.getDownloader();
        this.databaseService = new DatabaseServiceImpl();
        this.databaseBackupService = new BackupServiceImpl(databaseService);
        this.server = new Server();
    }

    public static MyJFQL getInstance() {
        return instance;
    }

    public void start() {
        console.print("\n      _                  ______ _ _       ____                        _                                              \n" +
                "     | |                |  ____(_) |     / __ \\                      | |                                             \n" +
                "     | | __ ___   ____ _| |__   _| | ___| |  | |_   _  ___ _ __ _   _| |     __ _ _ __   __ _ _   _  __ _  __ _  ___ \n" +
                " _   | |/ _` \\ \\ / / _` |  __| | | |/ _ \\ |  | | | | |/ _ \\ '__| | | | |    / _` | '_ \\ / _` | | | |/ _` |/ _` |/ _ \\\n" +
                "| |__| | (_| |\\ V / (_| | |    | | |  __/ |__| | |_| |  __/ |  | |_| | |___| (_| | | | | (_| | |_| | (_| | (_| |  __/\n" +
                " \\____/ \\__,_| \\_/ \\__,_|_|    |_|_|\\___|\\___\\_\\\\__,_|\\___|_|   \\__, |______\\__,_|_| |_|\\__, |\\__,_|\\__,_|\\__, |\\___|\n" +
                "                                                                 __/ |                   __/ |             __/ |     \n" +
                "                                                                |___/                   |___/             |___/      \n" +
                "");
        console.logInfo("Developer > ByJoker");
        console.logInfo("Version > v" + version);
        console.clean();

        try {
            console.logInfo("Loading system configurations...");

            {
                configService.load();
                configService.searchConfigBuilders("de.byjoker.myjfql.config");
                config = configService.getConfig();
            }

            if (config.jline()) {
                console = new SimpleConsole();
            }

            if ("ARGON2".equalsIgnoreCase(config.encryption())) {
                encryptor = new Argon2Encryptor();
            } else {
                encryptor = new NoneEncryptor();
            }

            console.logInfo("Successfully initialized config.");
            console.clean();
        } catch (Exception ex) {
            throw new FileException("Failed to load and initialize config!");
        }

        if (config.updates()) {
            console.logInfo("Connecting to " + config.updateHost() + "...");

            try {
                updater.connect(config.updateHost());
            } catch (Exception ex) {
                throw new NetworkException("Server connection failed!");
            }

            console.logInfo("Successfully connected.");
            console.clean();

            switch (updater.getCompatibilityStatus()) {
                case SAME:
                    console.logInfo("Your are up to date with you MyJFQL version. You can enjoy all features of this system :D");
                    break;
                case JUST_FINE:
                    if (config.autoUpdate())
                        downloader.downloadLatestVersion();
                    else
                        console.logWarning("You aren't up to date. Please download the latest version.");
                    break;
                case SOME_CHANGES:
                    console.logWarning("You aren't up to date. Please download the latest version. But please make sure that the new version working you have to make some changes!");
                    break;
                case PENSIONER:
                    console.logWarning("You are using a pretty old version of MyJFQL! With this version you wouldn't be able to update to the latest version without many heavy changes.");
                    break;
            }

            console.clean();
        }

        commandService.searchCommands("de.byjoker.myjfql.command");

        if (config.server()) {
            try {
                server.start(config.port());
            } catch (Exception ex) {
                throw new NetworkException("Failed to start javalin server because " + ex.getMessage());
            }

            console.clean();
        }

        {
            console.logInfo("Loading databases and users (This can take a while)...");

            try {
                databaseService.loadAll();
                userService.loadAll();
                sessionService.loadAll();
            } catch (Exception ex) {
                throw new FileException(ex);
            }

            console.logInfo("Loading finished!");
            console.clean();
        }


        {
            if (encryptor.name().equals("NONE")) {
                console.logWarning("You are using no encryption! This state of password storing is very insecure!");
            }

            if (databaseService.getDatabases().stream().anyMatch(database -> database.getType() == DatabaseType.SINGLE_STORAGE_TARGET)) {
                console.logWarning("Some of your databases are single file databases! This can affect you scalability!");
            }

            if (databaseService.getDatabases().size() == 0) {
                console.logWarning("No databases exists!");
            }

            if (userService.getUsers().size() == 0) {
                console.logWarning("No users exists!");
            }
        }

        sessionService.openSession(
                new Session(consoleCommandSender.getName(), consoleCommandSender.getName(), null, "localhost", System.currentTimeMillis(), -1)
        );

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                databaseService.updateAll();
                userService.updateAll();
                sessionService.collectExpiresSessions();
                sessionService.updateAll();
            }
        }, 1000 * 60, 1000 * 60);

        console.bind(new ConsoleCommandCompleter(commandService, consoleCommandSender));

        while (true)
            commandService.execute(consoleCommandSender, console.readPrompt());
    }

    public void shutdown() {
        try {
            console.logInfo("Shutdown (This can take a while)...");
            databaseService.updateAll();
            userService.updateAll();
            sessionService.updateAll();
            server.shutdown();
        } catch (Exception ignore) {
        }

        System.exit(0);
    }

    public Console getConsole() {
        return console;
    }

    public ConsoleCommandSender getConsoleCommandSender() {
        return consoleCommandSender;
    }

    public Updater getUpdater() {
        return updater;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public String getVersion() {
        return version;
    }

    public CommandService getCommandService() {
        return commandService;
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public Server getServer() {
        return server;
    }

    public Encryptor getEncryptor() {
        return encryptor;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public UserService getUserService() {
        return userService;
    }

    public BackupService getDatabaseBackupService() {
        return databaseBackupService;
    }

    public Config getConfig() {
        return config;
    }

    public SessionService getSessionService() {
        return sessionService;
    }
}
