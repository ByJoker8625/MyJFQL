package de.byjoker.myjfql.database

import java.time.LocalDate

interface Database {

    var id: String
    var name: String
    var type: Type
    var changes: MutableList<String>
    fun pushTable(table: Table)
    fun pullTable(uniqueId: String): Table?
    fun pullTableByName(name: String): Table?
    fun popTable(uniqueId: String)
    fun getTables(): List<Table>
    var createdAt: LocalDate

    enum class Type(val identifier: List<String>) {
        INTERNAL(listOf("internal", "intern")),
        STANDALONE(listOf("standalone", "singleton")),
        HALF_DOCUMENT(listOf("half_document")), DOCUMENT(listOf("document"))
    }

}
