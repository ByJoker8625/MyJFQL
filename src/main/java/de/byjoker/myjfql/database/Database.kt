package de.byjoker.myjfql.database

interface Database {

    fun createTable(table: Table)
    fun saveTable(table: Table)
    fun getTable(tableId: String): Table?
    fun getTableByName(name: String): Table?
    fun getTableByIdentifier(identifier: String): Table?
    fun deleteTable(tableId: String)
    fun getTables(): List<Table>
    fun format(type: DatabaseType, databaseService: DatabaseService)
    fun setName(name: String)
    fun getType(): DatabaseType
    fun getName(): String
    fun getId(): String


}
