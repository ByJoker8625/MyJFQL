package de.byjoker.myjfql.core

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.byjoker.myjfql.command.CommandService
import de.byjoker.myjfql.command.CommandServiceImpl
import de.byjoker.myjfql.command.ConsoleCommandSender
import de.byjoker.myjfql.config.ConfigService
import de.byjoker.myjfql.config.GeneralConfig
import de.byjoker.myjfql.config.YamlConfigService
import de.byjoker.myjfql.database.*
import de.byjoker.myjfql.lang.Interpreter
import de.byjoker.myjfql.lang.JFQLInterpreter
import de.byjoker.myjfql.network.HttpNetworkService
import de.byjoker.myjfql.network.NetworkService
import de.byjoker.myjfql.network.session.InternalSession
import de.byjoker.myjfql.network.session.SessionService
import de.byjoker.myjfql.network.session.SessionServiceImpl
import de.byjoker.myjfql.network.util.Response
import de.byjoker.myjfql.user.UserService
import de.byjoker.myjfql.util.Cache
import de.byjoker.myjfql.util.IDGenerator
import de.byjoker.myjfql.util.QueryCache
import org.slf4j.LoggerFactory
import java.util.stream.IntStream
import kotlin.system.exitProcess

fun main() {
    MyJFQL.getInstance().start()
}

class MyJFQL private constructor() {

    private val logger = LoggerFactory.getLogger("de.byjoker.myjfql")

    val configService: ConfigService
    val config: GeneralConfig
    val commandService: CommandService
    val networkService: NetworkService
    val sessionService: SessionService
    val databaseService: DatabaseService
    val userService: UserService? = null
    val interpreter: Interpreter
    val cache: Cache<String, Response>

    init {
        instance = this
        configService = YamlConfigService()
        config = configService.loadMapped()
        sessionService = SessionServiceImpl()
        commandService = CommandServiceImpl()
        networkService = HttpNetworkService()
        databaseService = DatabaseServiceImpl()
        interpreter = JFQLInterpreter(commandService)
        cache = QueryCache()
    }

    fun start() {
        commandService.searchCommands("de.byjoker.myjfql.command")

        databaseService.loadAll()

        /*
        val database = SimpleDatabase(name = "test", type = DatabaseType.SHARDED)
        val users = RelationalTable(
            name = "users",
            databaseId = database.id,
            structure = mutableListOf("id", "name", "password", "email")
        )

        val factory = JsonNodeFactory.instance

        for (i in IntStream.range(0, 100)) {
            users.pushEntry(
                RelationalEntry()
                    .insert("id", factory.textNode(IDGenerator.generateDigits(5)))
                    .insert("name", factory.textNode(IDGenerator.generateString(6)))
                    .insert("password", factory.textNode(IDGenerator.generateMixed(12)))
                    .insert(
                        "email",
                        factory.textNode("${IDGenerator.generateString(4)}@${IDGenerator.generateString(5)}.com")
                    )
            )
        }

        database.pushTable(users)

        databaseService.saveDatabase(database)
        databaseService.writeAll()*/

    }

    fun shutdown() {
        try {
        } catch (ex: Exception) {
            logger.error(ex.message, ex)
        }

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
