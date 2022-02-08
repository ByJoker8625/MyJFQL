package de.byjoker.myjfql.command

import de.byjoker.myjfql.database.DatabaseActionPerformType
import de.byjoker.myjfql.database.TableEntry
import de.byjoker.myjfql.server.session.Session
import de.byjoker.myjfql.util.ResultType

abstract class CommandSender(open var name: String, open var session: Session?) {

    abstract fun allowed(database: String, action: DatabaseActionPerformType): Boolean

    abstract fun sendError(obj: Any?)
    abstract fun sendForbidden()
    abstract fun sendSyntax()
    abstract fun sendSuccess()
    abstract fun sendResult(entries: MutableCollection<TableEntry>, structure: MutableCollection<String>, resultType: ResultType)
    abstract fun send(obj: Any?)

}
