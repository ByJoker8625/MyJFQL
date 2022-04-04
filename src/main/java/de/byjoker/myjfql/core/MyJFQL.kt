package de.byjoker.myjfql.core

import de.byjoker.myjfql.command.*
import de.byjoker.myjfql.config.ConfigService
import de.byjoker.myjfql.config.GeneralConfig
import de.byjoker.myjfql.config.YamlConfigService
import de.byjoker.myjfql.console.Console
import de.byjoker.myjfql.console.ConsoleCommandCompleter
import de.byjoker.myjfql.console.ConsoleImpl
import de.byjoker.myjfql.console.SimpleJLineConsole
import de.byjoker.myjfql.database.DatabaseService
import de.byjoker.myjfql.database.DatabaseServiceImpl
import de.byjoker.myjfql.database.DatabaseType
import de.byjoker.myjfql.database.SimpleDatabase
import de.byjoker.myjfql.lang.Interpreter
import de.byjoker.myjfql.lang.JFQLInterpreter
import de.byjoker.myjfql.network.HttpNetworkService
import de.byjoker.myjfql.network.NetworkService
import de.byjoker.myjfql.network.session.RelationalSessionService
import de.byjoker.myjfql.network.session.SessionService
import de.byjoker.myjfql.network.util.Response
import de.byjoker.myjfql.user.RelationalUserService
import de.byjoker.myjfql.user.UserService
import de.byjoker.myjfql.user.UsersTable
import de.byjoker.myjfql.util.*
import kotlin.system.exitProcess

fun main() {
    MyJFQL.getInstance().start()
}

class MyJFQL private constructor() {

    val version: String = "1.6.0"
    val configService: ConfigService
    val consoleCommandSender: ConsoleCommandSender
    val commandService: CommandService
    val networkService: NetworkService
    val sessionService: SessionService
    val databaseService: DatabaseService
    val userService: UserService
    val interpreter: Interpreter
    val cache: Cache<String, Response>
    var config: GeneralConfig
    var encryptor: Encryptor
    var console: Console

    init {
        instance = this
        configService = YamlConfigService()
        config = configService.defaults()
        sessionService = RelationalSessionService()
        commandService = CommandServiceImpl()
        networkService = HttpNetworkService()
        databaseService = DatabaseServiceImpl()
        userService = RelationalUserService()
        interpreter = JFQLInterpreter(commandService)
        encryptor = NoneEncryptor()
        console = ConsoleImpl()
        consoleCommandSender = ConsoleCommandSender()
        cache = QueryCache()
    }

    fun start() {
        println(
            " /\$\$      /\$\$              /\$\$\$\$\$ /\$\$\$\$\$\$\$\$ /\$\$\$\$\$\$  /\$\$      \n| \$\$\$    /\$\$\$             |__  \$\$| \$\$_____//\$\$__  \$\$| \$\$      \n| \$\$\$\$  /\$\$\$\$ /\$\$   /\$\$      | \$\$| \$\$     | \$\$  \\ \$\$| \$\$      \n| \$\$ \$\$/\$\$ \$\$| \$\$  | \$\$      | \$\$| \$\$\$\$\$  | \$\$  | \$\$| \$\$      \n| \$\$  \$\$\$| \$\$| \$\$  | \$\$ /\$\$  | \$\$| \$\$__/  | \$\$  | \$\$| \$\$      \n| \$\$\\  \$ | \$\$| \$\$  | \$\$| \$\$  | \$\$| \$\$     | \$\$/\$\$ \$\$| \$\$      \n| \$\$ \\/  | \$\$|  \$\$\$\$\$\$\$|  \$\$\$\$\$\$/| \$\$     |  \$\$\$\$\$\$/| \$\$\$\$\$\$\$\$\n|__/     |__/ \\____  \$\$ \\______/ |__/      \\____ \$\$\$|________/\n              /\$\$  | \$\$                         \\__/          \n             |  \$\$\$\$\$\$/                                       \n              \\______/    "
        )
        console.info("Starting MyJFQL v$version...")

        console.info("Initializing configuration...")
        config = try {
            configService.loadMapped()
        } catch (ex: Exception) {
            console.error("Failed at config initialization!")
            exitProcess(-1)
        }
        console.info("Finished configuration initialization.")

        when (encryptor.name) {
            "NONE" -> NoneEncryptor()
            "ARGON2" -> Argon2Encryptor(config.salt)
        }

        console.info("Initializing databases...")
        try {
            databaseService.loadAll()
        } catch (ex: Exception) {
            console.error("Failed at database initialization!")
            exitProcess(-1)
        }
        console.info("Finished database initialization.")

        var internal = databaseService.getDatabaseByName("internal")
        if (internal == null) {
            console.info("Setting up internal database...")
            try {
                internal = SimpleDatabase("internal", "internal", DatabaseType.INTERNAL)
                internal.saveTable(UsersTable())

                databaseService.saveDatabase(internal)
            } catch (ex: Exception) {
                console.error("Failed with internal database setup!")
                exitProcess(-1)
            }
            console.info("Finished internal database setup.")
        }
        userService.load(internal.getTable("users") as UsersTable)

        commandService.registerCommand(ShutdownCommand())
        commandService.registerCommand(PushCommand())

        if (config.jline) {
            console = SimpleJLineConsole(console)
            console.bind(ConsoleCommandCompleter(commandService, consoleCommandSender))
        }

        console.info("MyJFQL is now up and running.")

        while (true) try {
            commandService.execute(consoleCommandSender, console.readPrompt())
        } catch (ex: Exception) {
            console.error(ex)
        }
    }

    fun shutdown() {
        console.info("Exiting and saving all running processes...")

        try {
            databaseService.writeAll()
        } catch (ex: Exception) {
            console.error(ex)
        }

        console.info("Shutdown MyJFQL!")
        exitProcess(0)
    }

    companion object {
        private var instance: MyJFQL? = null

        fun getInstance(): MyJFQL {
            return when (instance) {
                null -> MyJFQL()
                else -> instance!!
            }
        }
    }

}
