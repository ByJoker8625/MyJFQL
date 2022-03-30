package de.byjoker.myjfql.database

import de.byjoker.myjfql.lang.Requirement
import java.time.LocalDate

interface Table {

    var id: String
    var name: String
    var databaseId: String
    var type: Type
    var changes: MutableList<String>
    fun pushEntry(entry: Entry)
    fun pullEntry(uniqueId: String): Entry?
    fun popEntry(uniqueId: String)
    fun find(conditions: List<List<Requirement>>): List<Entry>?
    fun getEntries(): List<Entry>
    var createdAt: LocalDate

    enum class Type(val identifier: List<String>) {
        INTERNAL(listOf("internal", "intern")), RELATIONAL(listOf("relational")), DOCUMENT(
            listOf(
                "document",
                "non-relational"
            )
        )
    }

}
