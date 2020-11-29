package de.jokergames.jfql.core;

import de.jokergames.jfql.command.*;
import de.jokergames.jfql.core.lang.ConditionHelper;
import de.jokergames.jfql.core.lang.Formatter;
import de.jokergames.jfql.core.script.Script;
import de.jokergames.jfql.core.script.ScriptService;
import de.jokergames.jfql.database.DBSession;
import de.jokergames.jfql.database.DatabaseHandler;
import de.jokergames.jfql.event.ClientLoginEvent;
import de.jokergames.jfql.event.CommandExecuteEvent;
import de.jokergames.jfql.event.EventService;
import de.jokergames.jfql.event.InvokeScriptEvent;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.exception.EventException;
import de.jokergames.jfql.exception.ModuleException;
import de.jokergames.jfql.exception.NetworkException;
import de.jokergames.jfql.jvl.JavalinService;
import de.jokergames.jfql.module.ModuleHandler;
import de.jokergames.jfql.user.ConsoleUser;
import de.jokergames.jfql.user.UserHandler;
import de.jokergames.jfql.util.ConfigHandler;
import de.jokergames.jfql.util.Connection;
import de.jokergames.jfql.util.Console;
import de.jokergames.jfql.util.Downloader;
import org.json.JSONObject;

/**
 * @author Janick
 */

public final class JFQL {

    private static JFQL instance;

    private final Console console;
    private final CommandService commandService;
    private final ConfigHandler configHandler;
    private final Formatter formatter;
    private final String version;
    private final Downloader downloader;
    private final Connection connection;
    private final JSONObject configuration;
    private final DatabaseHandler dataBaseHandler;
    private final UserHandler userHandler;
    private final DBSession dbSession;
    private final ScriptService scriptService;
    private final ModuleHandler moduleHandler;
    private final EventService eventService;
    private final ConditionHelper conditionHelper;
    private JavalinService javalinService;

    public JFQL() {
        instance = this;

        this.version = "1.2.3-BETA";
        this.console = new Console();
        this.connection = new Connection();
        this.downloader = new Downloader(connection);
        this.formatter = new Formatter();
        this.configHandler = new ConfigHandler();
        this.eventService = new EventService();
        this.moduleHandler = new ModuleHandler();
        this.conditionHelper = new ConditionHelper();
        this.commandService = new CommandService();
        this.dbSession = new DBSession();
        this.scriptService = new ScriptService(configHandler.getFactory());
        this.dataBaseHandler = new DatabaseHandler(configHandler.getFactory());
        this.configuration = configHandler.getConfig();
        this.userHandler = new UserHandler(configHandler.getFactory());
    }

    public static JFQL getInstance() {
        return instance;
    }

    public void start() {
        console.clean("    _                  ______ _ _       ____                        _                                                    \n" +
                "     | |                |  ____(_) |     / __ \\                      | |                                                   \n" +
                "     | | __ ___   ____ _| |__   _| | ___| |  | |_   _  ___ _ __ _   _| |     __ _ _ __   __ _ _   _  __ _ _ __   __ _  ___ \n" +
                " _   | |/ _` \\ \\ / / _` |  __| | | |/ _ \\ |  | | | | |/ _ \\ '__| | | | |    / _` | '_ \\ / _` | | | |/ _` | '_ \\ / _` |/ _ \\\n" +
                "| |__| | (_| |\\ V / (_| | |    | | |  __/ |__| | |_| |  __/ |  | |_| | |___| (_| | | | | (_| | |_| | (_| | | | | (_| |  __/\n" +
                " \\____/ \\__,_| \\_/ \\__,_|_|    |_|_|\\___|\\___\\_\\\\__,_|\\___|_|   \\__, |______\\__,_|_| |_|\\__, |\\__,_|\\__,_|_| |_|\\__, |\\___|\n" +
                "                                                                 __/ |                   __/ |                   __/ |     \n" +
                "                                                                |___/                   |___/                   |___/      ");
        console.logInfo("Developers » jokergames.ddnss.de");
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
                if (configuration.getBoolean("AutoUpdate")) {
                    downloader.download();
                } else {
                    console.logWarning("You aren't up to date. Please download the latest version.");
                }
            }
        }

        {
            if (userHandler.getUser("Console") == null)
                userHandler.saveUser(new ConsoleUser());
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
            commandService.registerCommand(new UsrCommand());
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
            moduleHandler.enableModules();
        } catch (Exception ex) {
            throw new ModuleException("Can't load modules!");
        }

        if (moduleHandler.getModules().size() != 0) {
            console.clean();
        }

        try {
            javalinService = new JavalinService();
        } catch (Exception ex) {
            throw new NetworkException("Can't start javalin server");
        }


        {
            if (configHandler.isCrt()) {
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
        moduleHandler.disableModules();
        System.exit(0);
    }

    public ConsoleUser getConsoleUser() {
        return (ConsoleUser) userHandler.getUser("Console");
    }

    public ModuleHandler getModuleHandler() {
        return moduleHandler;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
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

    public DatabaseHandler getDataBaseHandler() {
        return dataBaseHandler;
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public DBSession getDbSession() {
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

    public JavalinService getJavalinService() {
        return javalinService;
    }

    public Console getConsole() {
        return console;
    }
}
