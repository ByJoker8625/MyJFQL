package de.byjoker.myjfql.network.session

import de.byjoker.myjfql.database.Database
import de.byjoker.myjfql.database.DatabaseService
import de.byjoker.myjfql.database.RelationalTableEntry
import de.byjoker.myjfql.database.TableEntry

abstract class Session(
    val token: String,
    val type: SessionType,
    var userId: String,
    var databaseId: String?,
    var addresses: MutableList<String>
) {

    fun getDatabase(databaseService: DatabaseService): Database? {
        return databaseService.getDatabase(databaseId)
    }

    abstract fun validAddress(address: String): Boolean

    fun asTableEntry(): TableEntry {
        val entry = RelationalTableEntry()
        entry.insert("token", token)
        entry.insert("type", type)
        entry.insert("user_id", userId)
        entry.insert("database_id", databaseId)
        entry.insert("addresses", addresses)

        return entry
    }

}
