package de.byjoker.myjfql.command

import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.network.session.Session
import de.byjoker.myjfql.user.UserType

interface CommandSender {

    val name: String
    val session: Session?
    fun permitted(action: DatabasePermissionLevel, databaseId: String): Boolean
    fun permitted(action: UserType): Boolean
    fun success()
    fun forbidden() = error("No permission!")
    fun syntax() = error("Unknown syntax!")
    fun result(result: Any)
    fun error(exception: Exception)
    fun error(exception: String)

}
