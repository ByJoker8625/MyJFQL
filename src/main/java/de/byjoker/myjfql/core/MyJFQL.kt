package de.byjoker.myjfql.core

import de.byjoker.myjfql.command.CommandService
import de.byjoker.myjfql.command.CommandServiceImpl
import de.byjoker.myjfql.command.ConsoleCommandSender
import de.byjoker.myjfql.config.ConfigService
import de.byjoker.myjfql.config.GeneralConfig
import de.byjoker.myjfql.config.YamlConfigService
import de.byjoker.myjfql.console.Console
import de.byjoker.myjfql.console.ConsoleCommandCompleter
import de.byjoker.myjfql.console.ConsoleImpl
import de.byjoker.myjfql.console.SimpleJLineConsole
import de.byjoker.myjfql.database.*
import de.byjoker.myjfql.lang.Interpreter
import de.byjoker.myjfql.lang.JFQLInterpreter
import de.byjoker.myjfql.network.HttpNetworkService
import de.byjoker.myjfql.network.NetworkService
import de.byjoker.myjfql.network.session.SessionService
import de.byjoker.myjfql.network.session.SessionServiceImpl
import de.byjoker.myjfql.network.util.Response
import de.byjoker.myjfql.user.UserService
import de.byjoker.myjfql.util.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.system.exitProcess

fun main() {
    MyJFQL.getInstance().start()
}

@DelicateCoroutinesApi
class MyJFQL private constructor() {

    val version: String = "1.6.0"
    val configService: ConfigService
    val consoleCommandSender: ConsoleCommandSender
    val commandService: CommandService
    val networkService: NetworkService
    val sessionService: SessionService
    val databaseService: DatabaseService
    val userService: UserService? = null
    val interpreter: Interpreter
    val cache: Cache<String, Response>
    var config: GeneralConfig
    var encryptor: Encryptor
    var console: Console

    init {
        instance = this
        configService = YamlConfigService()
        config = configService.defaults()
        sessionService = SessionServiceImpl()
        commandService = CommandServiceImpl()
        networkService = HttpNetworkService()
        databaseService = DatabaseServiceImpl()
        interpreter = JFQLInterpreter(commandService)
        encryptor = NoneEncryptor()
        console = ConsoleImpl()
        consoleCommandSender = ConsoleCommandSender()
        cache = QueryCache()
    }

    fun start() {
        println(
            " /\$\$      /\$\$              /\$\$\$\$\$ /\$\$\$\$\$\$\$\$ /\$\$\$\$\$\$  /\$\$      \n" + "| \$\$\$    /\$\$\$             |__  \$\$| \$\$_____//\$\$__  \$\$| \$\$      \n" + "| \$\$\$\$  /\$\$\$\$ /\$\$   /\$\$      | \$\$| \$\$     | \$\$  \\ \$\$| \$\$      \n" + "| \$\$ \$\$/\$\$ \$\$| \$\$  | \$\$      | \$\$| \$\$\$\$\$  | \$\$  | \$\$| \$\$      \n" + "| \$\$  \$\$\$| \$\$| \$\$  | \$\$ /\$\$  | \$\$| \$\$__/  | \$\$  | \$\$| \$\$      \n" + "| \$\$\\  \$ | \$\$| \$\$  | \$\$| \$\$  | \$\$| \$\$     | \$\$/\$\$ \$\$| \$\$      \n" + "| \$\$ \\/  | \$\$|  \$\$\$\$\$\$\$|  \$\$\$\$\$\$/| \$\$     |  \$\$\$\$\$\$/| \$\$\$\$\$\$\$\$\n" + "|__/     |__/ \\____  \$\$ \\______/ |__/      \\____ \$\$\$|________/\n" + "              /\$\$  | \$\$                         \\__/          \n" + "             |  \$\$\$\$\$\$/                                       \n" + "              \\______/    "
        )
        console.info("Starting MyJFQL v$version...")

        console.info("Initializing configuration...")
        config = try {
            configService.loadMapped()
        } catch (ex: Exception) {
            console.error("Failed ×")
            exitProcess(-1)
        }
        console.info("Finished ✓")

        commandService.searchCommands("de.byjoker.myjfql.command")
        databaseService.loadAll()

        when (encryptor.name) {
            "NONE" -> NoneEncryptor()
            "ARGON2" -> Argon2Encryptor(config.salt)
        }

        if (config.jline) {
            console = SimpleJLineConsole(console)
            console.bind(ConsoleCommandCompleter(commandService, consoleCommandSender))
        }

        if (databaseService.getDatabaseByName("internal") == null) {
            console.info("Setting up 'internal' database...")

            val internal = SimpleDatabase("internal", "internal", DatabaseType.INTERNAL)
            val users = DocumentCollection("users", "users", databaseId = internal.id)
            val sessions = DocumentCollection("sessions", "sessions", databaseId = internal.id)


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
