package de.byjoker.myjfql.network.util

import de.byjoker.myjfql.network.session.Session

interface Connection {

    val address: String
    val session: Session?
    fun respond(response: Response)
    fun close()

}
