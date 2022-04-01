package de.byjoker.myjfql.database

import java.io.File

class JsonDatabaseService() : DatabaseService {

    private val databases: MutableMap<String, Database> = mutableMapOf()

    override fun createDatabase(database: Database) {
        TODO("Not yet implemented")
    }

    override fun saveDatabase(database: Database) {
        databases[database.id] = database
    }

    override fun deleteDatabase(id: String) {
        TODO("Not yet implemented")
    }

    override fun getDatabase(id: String): Database? {
        return databases[id]
    }

    override fun getDatabaseByName(name: String): Database? {
        return databases.values.firstOrNull { database -> database.name == name }
    }

    override fun getDatabases(): List<Database> {
        return databases.values.toList()
    }


}
