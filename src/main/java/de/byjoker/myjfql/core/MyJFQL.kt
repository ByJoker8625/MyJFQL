package de.byjoker.myjfql.core

import de.byjoker.myjfql.command.CommandService
import de.byjoker.myjfql.command.CommandServiceImpl
import de.byjoker.myjfql.command.ConsoleCommandSender
import de.byjoker.myjfql.config.ConfigService
import de.byjoker.myjfql.config.GeneralConfig
import de.byjoker.myjfql.config.YamlConfigService
import de.byjoker.myjfql.database.DatabaseService
import de.byjoker.myjfql.lang.Interpreter
import de.byjoker.myjfql.lang.JFQLInterpreter
import de.byjoker.myjfql.network.HttpNetworkService
import de.byjoker.myjfql.network.NetworkService
import de.byjoker.myjfql.network.cluster.Cluster
import de.byjoker.myjfql.network.cluster.StandaloneCluster
import de.byjoker.myjfql.network.session.InternalSession
import de.byjoker.myjfql.network.session.SessionService
import de.byjoker.myjfql.network.session.SessionServiceImpl
import de.byjoker.myjfql.network.util.Response
import de.byjoker.myjfql.user.UserService
import de.byjoker.myjfql.util.Cache
import de.byjoker.myjfql.util.QueryCache
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.system.exitProcess

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
    val cluster: Cluster
    val cache: Cache<String, Response>

    init {
        instance = this
        configService = YamlConfigService()
        config = configService.loadMapped()
        sessionService = SessionServiceImpl()
        commandService = CommandServiceImpl()
        networkService = HttpNetworkService()
        interpreter = JFQLInterpreter(commandService)
        cluster = StandaloneCluster()
        cache = QueryCache()
    }

    fun start() {
        cluster.join()

        while (true) {
            commandService.execute(
                ConsoleCommandSender("Console", InternalSession("Console")),
                Scanner(System.`in`).nextLine()
            )
        }
    }

    fun shutdown() {
        try {
            cluster.quit()
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
