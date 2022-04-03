package de.byjoker.myjfql.database

import com.fasterxml.jackson.annotation.JsonIgnore
import de.byjoker.myjfql.lang.Requirement

interface Table {

    val id: String
    var name: String

    val structure: List<String>
    val primary: String
    val partitioner: String
    val type: TableType
    fun pushEntry(entry: Entry)
    fun getEntry(entryId: String): Entry?
    fun removeEntry(entryId: String)
    fun findEntries(conditions: List<List<Requirement>>, limit: Int): List<Entry>
    fun getEntries(): List<Entry>
    fun clear()

    @get:JsonIgnore
    val databaseId: String

}
