package de.byjoker.myjfql.database

interface Database {
    val tables: Collection<Table>
    val type: DatabaseType
    val name: String
    val id: String
    fun regenerateId()
    fun createTable(table: Table)
    fun saveTable(table: Table)
    fun existsTable(name: String): Boolean
    fun deleteTable(name: String)
    fun reformat(type: DatabaseType, service: DatabaseService)
    fun getTable(name: String): Table?
}
