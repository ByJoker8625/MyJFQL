package de.byjoker.myjfql.network.session

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.Database
import de.byjoker.myjfql.database.DatabaseService

abstract class Session(
    val token: String,
    val type: SessionType,
    var userId: String,
    var databaseId: String?,
    var addresses: MutableList<String>
) {

    abstract fun validAddress(address: String): Boolean

    fun getDatabase(databaseService: DatabaseService = MyJFQL.getInstance().databaseService): Database? {
        return if (databaseId == null) null else databaseService.getDatabase(databaseId!!)
    }

    override fun toString(): String {
        return "Session(token='$token', type=$type, userId='$userId', databaseId=$databaseId, addresses=$addresses)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Session

        if (token != other.token) return false

        return true
    }

    override fun hashCode(): Int {
        return token.hashCode()
    }

}
