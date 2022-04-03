package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.network.session.Session
import de.byjoker.myjfql.user.UserType

class ConsoleCommandSender(override val name: String) : CommandSender {

    override val session: Session
        get() = MyJFQL.getInstance().sessionService.getSession("%CONSOLE%")!!

    override fun permitted(action: DatabasePermissionLevel, databaseId: String): Boolean {
        return true
    }

    override fun permitted(action: UserType): Boolean {
        return true
    }

    override fun success() {
    }

    override fun forbidden() {
    }

    override fun result(result: Any) {
    }

    override fun error(exception: Exception) {
    }

    override fun error(exception: String) {
    }

}
