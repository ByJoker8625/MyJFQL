package de.byjoker.myjfql.database

import de.byjoker.myjfql.util.StorageService

interface DatabaseService : StorageService<Database> {

    fun createDatabase(database: Database)
    fun saveDatabase(database: Database)
    fun deleteDatabase(id: String)
    fun getDatabase(id: String): Database?
    fun getDatabaseByName(name: String): Database?
    fun getDatabaseByIdentifier(identifier: String): Database?
    fun getDatabases(): List<Database>

}
