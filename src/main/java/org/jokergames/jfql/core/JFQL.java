package org.jokergames.jfql.core;

import de.jokergames.jfql.command.*;
import org.jokergames.jfql.command.*;
import org.jokergames.jfql.core.lang.Formatter;
import org.jokergames.jfql.core.script.Script;
import org.jokergames.jfql.core.script.ScriptService;
import org.jokergames.jfql.database.DBSession;
import org.jokergames.jfql.database.DatabaseService;
import org.jokergames.jfql.event.ClientLoginEvent;
import org.jokergames.jfql.event.CommandExecuteEvent;
import org.jokergames.jfql.event.EventService;
import org.jokergames.jfql.event.InvokeScriptEvent;
import org.jokergames.jfql.exception.CommandException;
import org.jokergames.jfql.exception.EventException;
import org.jokergames.jfql.exception.ModuleException;
import org.jokergames.jfql.exception.NetworkException;
import org.jokergames.jfql.module.ModuleService;
import org.jokergames.jfql.server.Server;
import org.jokergames.jfql.user.ConsoleUser;
import org.jokergames.jfql.user.UserService;
import de.jokergames.jfql.util.*;
import org.jokergames.jfql.util.*;
import org.json.JSONObject;

/**
 * @author Janick
 */

public final class JFQL {

    private static JFQL instance;

    private final Console console;
    private final CommandService commandService;
    private final ConfigService configService;
    private final Formatter formatter;
    private final String version;
    private final Downloader downloader;
    private final Connection connection;
    private final JSONObject configuration;
    private final DatabaseService dataBaseService;
    private final UserService userService;
    private final DBSession dbSession;
    private final ScriptService scriptService;
    private final ModuleService moduleService;
    private final EventService eventService;
    private final ConditionHelper conditionHelper;
    private Server server;

    public JFQL() {
        instance = this;

        this.version = "1.2.6";
        this.console = new Console();
        this.connection = new Connection();
        this.downloader = new Downloader(connection);
        this.formatter = new Formatter();
        this.configService = new ConfigService();
        this.eventService = new EventService();
        this.moduleService = new ModuleService();
        this.conditionHelper = new ConditionHelper();
        this.commandService = new CommandService();
        this.dbSession = new DBSession();
        this.scriptService = new ScriptService(configService.getFactory());
        this.dataBaseService = new DatabaseService(configService.getFactory());
        this.configuration = configService.getConfig();
        this.userService = new UserService(configService.getFactory());
    }

    public static JFQL getInstance() {
        return instance;
    }

    public void start() {
        console.println("      _                  ______ _ _       ____                        _                                                    \n" +
                "     | |                |  ____(_) |     / __ \\                      | |                                                   \n" +
                "     | | __ ___   ____ _| |__   _| | ___| |  | |_   _  ___ _ __ _   _| |     __ _ _ __   __ _ _   _  __ _ _ __   __ _  ___ \n" +
                " _   | |/ _` \\ \\ / / _` |  __| | | |/ _ \\ |  | | | | |/ _ \\ '__| | | | |    / _` | '_ \\ / _` | | | |/ _` | '_ \\ / _` |/ _ \\\n" +
                "| |__| | (_| |\\ V / (_| | |    | | |  __/ |__| | |_| |  __/ |  | |_| | |___| (_| | | | | (_| | |_| | (_| | | | | (_| |  __/\n" +
                " \\____/ \\__,_| \\_/ \\__,_|_|    |_|_|\\___|\\___\\_\\\\__,_|\\___|_|   \\__, |______\\__,_|_| |_|\\__, |\\__,_|\\__,_|_| |_|\\__, |\\___|\n" +
                "                                                                 __/ |                   __/ |                   __/ |     \n" +
                "                                                                |___/                   |___/                   |___/      ");
        console.logInfo("Developers » joker-games.org");
        console.logInfo("Version » " + version);
        console.clean();

        console.logInfo("Connecting to " + configuration.getString("Server") + "...");

        try {
            connection.connect(configuration.getString("Server"));

            if (connection.isMaintenance()) {
                console.logWarning("Database is currently in maintenance!");
                System.exit(0);
                return;
            }
        } catch (Exception ex) {
            throw new NetworkException("Server connection failed!");
        }

        {
            console.logInfo("Successfully connected.");
            console.clean();

            if (!connection.isLatest()) {
                if (configuration.getBoolean("AutoUpdate") && !connection.latestIsBeta()) {
                    downloader.download();
                } else {
                    console.logWarning("You aren't up to date. Please download the latest version.");
                }
            }
        }

        {
            if (userService.getUser("Console") == null)
                userService.saveUser(new ConsoleUser());
        }

        try {
            eventService.registerEvent(ClientLoginEvent.TYPE);
            eventService.registerEvent(CommandExecuteEvent.TYPE);
            eventService.registerEvent(InvokeScriptEvent.TYPE);
        } catch (Exception ex) {
            throw new EventException("Can't load events!");
        }

        try {
            commandService.registerCommand(new ShutdownCommand());
            commandService.registerCommand(new UserCommand());
            commandService.registerCommand(new VersionCommand());
            commandService.registerCommand(new ListCommand());
            commandService.registerCommand(new UseCommand());
            commandService.registerCommand(new InsertCommand());
            commandService.registerCommand(new CreateCommand());
            commandService.registerCommand(new DeleteCommand());
            commandService.registerCommand(new SelectCommand());
            commandService.registerCommand(new RemoveCommand());
            commandService.registerCommand(new InvokeCommand());
        } catch (Exception ex) {
            throw new CommandException("Can't load commands!");
        }

        try {
            moduleService.enableModules();
        } catch (Exception ex) {
            throw new ModuleException("Can't load modules!");
        }

        if (moduleService.getModules().size() != 0) {
            console.clean();
        }

        try {
            server = new Server();
        } catch (Exception ex) {
            throw new NetworkException("Can't start javalin server");
        }

        {
            if (configService.first()) {
                scriptService.saveScript(new Script("create_default_db", "create database test", "use database test"));
                scriptService.invokeScript("create_default_db", getConsoleUser(), false);
            }
        }

        console.clean();

        while (true) {
            commandService.execute(formatter.formatCommand(console.read()));
        }
    }

    public void shutdown() {
        moduleService.disableModules();
        System.exit(0);
    }

    public ConsoleUser getConsoleUser() {
        return (ConsoleUser) userService.getUser("Console");
    }

    public ModuleService getModuleService() {
        return moduleService;
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public String getVersion() {
        return version;
    }

    public Downloader getUpdater() {
        return downloader;
    }

    public JSONObject getConfiguration() {
        return configuration;
    }

    public Connection getConnection() {
        return connection;
    }

    public DatabaseService getDatabaseService() {
        return dataBaseService;
    }

    public UserService getUserService() {
        return userService;
    }

    public DBSession getDBSession() {
        return dbSession;
    }

    public CommandService getCommandService() {
        return commandService;
    }

    public ConditionHelper getConditionHelper() {
        return conditionHelper;
    }

    public ScriptService getScriptService() {
        return scriptService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public Server getServer() {
        return server;
    }

    public Console getConsole() {
        return console;
    }
}
