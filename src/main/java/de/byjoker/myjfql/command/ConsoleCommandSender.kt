package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.network.session.Session
import de.byjoker.myjfql.user.UserType
import de.byjoker.myjfql.util.Json

class ConsoleCommandSender : CommandSender {

    private val console = MyJFQL.getInstance().console
    override val name: String = "Console"
    override val session: Session?
        get() = MyJFQL.getInstance().sessionService.getSession("%console%")

    override fun permitted(action: DatabasePermissionLevel, databaseId: String): Boolean {
        return true
    }

    override fun permitted(action: UserType): Boolean {
        return true
    }

    override fun success() {
        console.info("Command was successfully executed.")
    }

    override fun result(result: Any) {
        console.info(Json.pretty(result))
    }

    override fun error(exception: Exception) {
        console.error(exception)
    }

    override fun error(exception: String) {
        console.error(exception)
    }


}
