package de.byjoker.myjfql.database

interface Database {

    val id: String
    val name: String
    val type: DatabaseType
    fun pushTable(table: Table)
    fun getTable(tableId: String): Table?
    fun getTableByName(name: String): Table?
    fun deleteTable(tableId: String)
    fun getTables(): List<Table>


}
