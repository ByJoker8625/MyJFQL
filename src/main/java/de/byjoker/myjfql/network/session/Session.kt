package de.byjoker.myjfql.network.session

import de.byjoker.myjfql.database.Database
import de.byjoker.myjfql.database.DatabaseService

abstract class Session(
    val token: String,
    val type: SessionType,
    var userId: String,
    var databaseId: String?,
    val addresses: MutableList<String>
) {

    fun getDatabase(databaseService: DatabaseService): Database? {
        return databaseService.getDatabase(databaseId)
    }

    abstract fun validAddress(address: String): Boolean

}
