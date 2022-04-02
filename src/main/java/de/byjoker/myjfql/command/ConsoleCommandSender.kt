package de.byjoker.myjfql.command

import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.network.session.Session
import de.byjoker.myjfql.user.UserType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ConsoleCommandSender(override val name: String, override val session: Session) : CommandSender {

    private val logger: Logger = LoggerFactory.getLogger("de.byjoker.myjfql")

    override fun permitted(action: DatabasePermissionLevel, databaseId: String): Boolean {
        return true
    }

    override fun permitted(action: UserType): Boolean {
        return true
    }

    override fun success() {
        logger.info("Command was successfully performed.")
    }

    override fun forbidden() {
        logger.error("You don't have permission to do that!")
    }

    override fun result(result: Any) {
        logger.info(result.toString())
    }

    override fun error(exception: Exception) {
        logger.error(exception.message, exception)
    }

    override fun error(exception: String) {
        logger.error(exception)
    }

}
