package de.byjoker.myjfql.core

import de.byjoker.myjfql.command.CommandService
import de.byjoker.myjfql.command.CommandServiceImpl
import de.byjoker.myjfql.config.ConfigService
import de.byjoker.myjfql.config.GeneralConfig
import de.byjoker.myjfql.config.YamlConfigService
import de.byjoker.myjfql.database.DatabaseService
import de.byjoker.myjfql.lang.Interpreter
import de.byjoker.myjfql.lang.JFQLInterpreter
import de.byjoker.myjfql.network.HttpNetworkService
import de.byjoker.myjfql.network.NetworkService
import de.byjoker.myjfql.network.session.SessionService
import de.byjoker.myjfql.network.session.SessionServiceImpl
import de.byjoker.myjfql.network.util.Response
import de.byjoker.myjfql.user.UserService
import de.byjoker.myjfql.util.Cache
import de.byjoker.myjfql.util.QueryCache
import org.slf4j.LoggerFactory

fun main() {
    MyJFQL.getInstance().start()
}

class MyJFQL private constructor() {

    private val logger = LoggerFactory.getLogger(javaClass)

    val configService: ConfigService
    val config: GeneralConfig
    val commandService: CommandService
    val networkService: NetworkService
    val sessionService: SessionService
    val databaseService: DatabaseService? = null
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
        interpreter = JFQLInterpreter(commandService)
        cache = QueryCache()
    }

    fun start() {
        logger.info("Starting MyJFQL...")
        networkService.start(2291)
    }

    fun shutdown() {

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
