package de.byjoker.myjfql.database

import de.byjoker.myjfql.util.StorageService

interface DatabaseService : StorageService {

    var changes: MutableList<String>

    fun saveDatabase()
    fun getDatabase(id: String): Database?
    fun getDatabaseByName(name: String): Database?

}
