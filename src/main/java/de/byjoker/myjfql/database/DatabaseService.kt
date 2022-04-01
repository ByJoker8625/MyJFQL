package de.byjoker.myjfql.database

interface DatabaseService {

    val databases: List<Database>
    fun createDatabase(database: Database)
    fun saveDatabase(database: Database)
    fun deleteDatabase(id: String)
    fun getDatabase(id: String): Database?
    fun getDatabaseByName(name: String): Database?
    fun loadDatabase(id: String): Database?
    fun loadDatabases()

}
