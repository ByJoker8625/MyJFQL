package de.byjoker.myjfql.database

import de.byjoker.myjfql.lang.Requirement
import de.byjoker.myjfql.util.PositionLimiter
import java.time.LocalDate

interface Table {

    var id: String
    var name: String
    var databaseId: String
    var type: TableType
    fun pushEntry(entry: Entry)
    fun getEntry(uniqueId: String): Entry?
    fun removeEntry(uniqueId: String)
    fun findEntries(conditions: List<List<Requirement>>, limiter: PositionLimiter): List<Entry>?
    fun getEntries(): List<Entry>
    fun clear()
    var createdAt: LocalDate

}
