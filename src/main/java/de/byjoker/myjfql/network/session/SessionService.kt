package de.byjoker.myjfql.network.session

interface SessionService {

    fun openSession(session: Session)

    fun closeSession(token: String)

    fun closeSessions(userId: String)

    fun saveSession(session: Session)

    fun getSession(token: String): Session?

    fun getSessionsByUserId(userId: String): List<Session>

    val sessions: MutableList<Session>

}
