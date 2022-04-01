package de.byjoker.myjfql.database

import java.time.LocalDate

interface Database {

    var id: String
    var name: String
    var type: DatabaseType
    fun pushTable(table: Table)
    fun getTable(uniqueId: String): Table?
    fun getTableByName(name: String): Table?
    fun deleteTable(uniqueId: String)
    fun getTables(): List<Table>
    var createdAt: LocalDate


}
