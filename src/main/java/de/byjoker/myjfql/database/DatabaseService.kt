package de.byjoker.myjfql.database

interface DatabaseService {

    fun createDatabase(database: Database)
    fun saveDatabase(database: Database)
    fun deleteDatabase(id: String)
    fun getDatabase(id: String): Database?
    fun getDatabaseByName(name: String): Database?
    fun getDatabases(): List<Database>

}
