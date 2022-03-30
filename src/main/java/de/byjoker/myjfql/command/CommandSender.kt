package de.byjoker.myjfql.command

import de.byjoker.myjfql.network.session.Session

interface CommandSender {

    val name: String
    val session: Session
    fun success()
    fun result(result: Any)
    fun error(exception: String)

}
