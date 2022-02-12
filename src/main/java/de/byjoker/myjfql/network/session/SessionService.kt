package de.byjoker.myjfql.network.session

import de.byjoker.myjfql.util.StorageService

interface SessionService : StorageService {

    fun openSession(session: Session)

    fun closeSession(token: String)

    fun closeSessions(userId: String)

    fun saveSession(session: Session)

    fun getSession(token: String): Session?

    fun getSessionsByUser(userId: String): MutableList<Session>

    val sessions: MutableList<Session>

}
