package org.jokergames.myjfql.core;

import org.jokergames.myjfql.command.*;
import org.jokergames.myjfql.database.DBSession;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseBackupService;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.exception.NetworkException;
import org.jokergames.myjfql.server.Server;
import org.jokergames.myjfql.user.UserService;
import org.jokergames.myjfql.util.ConfigService;
import org.jokergames.myjfql.util.Console;
import org.jokergames.myjfql.util.UpdateConnection;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public final class MyJFQL {

    private static MyJFQL instance;

    private final Console console;
    private final UpdateConnection updateConnection;
    private final UpdateConnection.Downloader downloader;
    private final ConsoleCommandSender consoleCommandSender;
    private final CommandService commandService;
    private final DatabaseService databaseService;
    private final ConfigService configService;
    private final DatabaseBackupService databaseBackupService;
    private final UserService userService;
    private final DBSession dbSession;
    private final Server server;

    private final String version = "1.4.6";

    private JSONObject configuration;
    private long lastRefresh;

    public MyJFQL() {
        instance = this;
        this.console = new Console();
        this.consoleCommandSender = new ConsoleCommandSender(console);
        this.updateConnection = new UpdateConnection();
        this.commandService = new CommandService();
        this.configService = new ConfigService();
        this.userService = new UserService(configService.getFactory());
        this.configuration = configService.getConfiguration();
        this.downloader = new UpdateConnection.Downloader(updateConnection);
        this.databaseService = new DatabaseService(configService.getFactory());
        this.dbSession = new DBSession(userService, databaseService);
        this.databaseBackupService = new DatabaseBackupService(databaseService);
        this.server = new Server();

        this.lastRefresh = -1;
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
        console.logInfo("Developer > joker-games.org");
        console.logInfo("Version > " + version);
        console.clean();

        {
            console.logInfo("Connecting to " + configuration.getString("UpdateServer") + "...");

            try {
                updateConnection.connect(configuration.getString("UpdateServer"));

                if (updateConnection.isMaintenance()) {
                    console.logError("Database is currently in maintenance!");
                    return;
                }
            } catch (Exception ex) {
                throw new NetworkException("Server connection failed!");
            }

            console.logInfo("Successfully connected.");
            console.clean();
        }

        {
            if (!updateConnection.isLatest()) {
                if (configuration.getBoolean("AutoUpdate")
                        && !updateConnection.latestIsBeta()) {
                    downloader.download();
                } else {
                    console.logInfo("You aren't up to date. Please download the latest version.");
                }
            }
        }

        {
            commandService.register(new ClearCommand());
            commandService.register(new ShutdownCommand());
            commandService.register(new RefreshCommand());
            commandService.register(new ReloadCommand());
            commandService.register(new StructureCommand());
            commandService.register(new ListCommand());
            commandService.register(new CreateCommand());
            commandService.register(new DeleteCommand());
            commandService.register(new UseCommand());
            commandService.register(new VersionCommand());
            commandService.register(new UserCommand());
            commandService.register(new BackupCommand());
            commandService.register(new InsertCommand());
            commandService.register(new SelectCommand());
            commandService.register(new RemoveCommand());
        }

        try {
            server.start(configuration.getInt("Port"));
        } catch (Exception ex) {
            throw new NetworkException(ex);
        }

        console.clean();

        {
            console.logInfo("Loading databases and users (This can take a while)...");
            databaseService.load();
            userService.load();
            console.logInfo("Loading finished!");
        }

        {
            if (databaseService.getDataBases().size() == 0
                    && userService.getUsers().size() != 0) {
                console.clean();
                console.logWarning("No databases exist!");
            }

            if (databaseService.getDataBases().size() != 0
                    && userService.getUsers().size() == 0) {
                console.clean();
                console.logWarning("No users exist!");
            }

            if (databaseService.getDataBases().size() == 0 && userService.getUsers().size() == 0) {
                console.clean();
                console.logWarning("No databases exist!");
                console.logWarning("No users exist!");
            }

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    refresh();
                }
            }, 1000 * 60, 1000 * 60);
        }

        if (configService.isFirst()) {
            databaseService.saveDataBase(new Database("test"));
        }

        console.clean();
        console.complete();

        while (true)
            commandService.execute(consoleCommandSender, console.readPrompt());
    }

    public void shutdown() {

        try {
            console.logInfo("Shutdown (This can take a while)...");
            databaseService.update();
            userService.update();
        } catch (Exception ex) {
            console.logError("Ignoring this exception: " + ex.getMessage());
        }

        System.exit(0);
    }

    public void refresh() {
        databaseService.update();
        userService.update();
        lastRefresh = System.currentTimeMillis();
    }

    public void reload() {
        reloadConfig();
        reloadUsers();
        reloadDatabases();
        restartServer();
    }

    public void restartServer() {
        server.setPort(configuration.getInt("Port"));
        server.restart();
    }

    public void reloadConfig() {
        configService.load();
        configuration = configService.getConfiguration();
    }

    public void reloadDatabases() {
        databaseService.load();
    }

    public void reloadUsers() {
        userService.load();
    }

    public Console getConsole() {
        return console;
    }

    public ConsoleCommandSender getConsoleCommandSender() {
        return consoleCommandSender;
    }

    public UpdateConnection getUpdateConnection() {
        return updateConnection;
    }

    public UpdateConnection.Downloader getDownloader() {
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

    public DBSession getDBSession() {
        return dbSession;
    }

    public long getLastRefresh() {
        return lastRefresh;
    }

    public Server getServer() {
        return server;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public UserService getUserService() {
        return userService;
    }

    public DatabaseBackupService getDatabaseBackupService() {
        return databaseBackupService;
    }

    public JSONObject getConfiguration() {
        return configuration;
    }
}
