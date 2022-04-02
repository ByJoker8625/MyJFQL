package de.byjoker.myjfql.database

import de.byjoker.myjfql.exception.DatabaseException
import de.byjoker.myjfql.util.Json
import java.io.File

class DatabaseServiceImpl : DatabaseService {

    private val databases: MutableMap<String, Database> = mutableMapOf()

    override fun createDatabase(database: Database) {
        if (databases.containsKey(database.id) || getDatabaseByName(database.name) != null) {
            throw DatabaseException("Database already exist!")
        }

        saveDatabase(database)
    }

    override fun saveDatabase(database: Database) {
        databases[database.id] = database
    }

    override fun deleteDatabase(id: String) {
        File("databases/$id").delete()
        databases.remove(id)
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

    override fun load(backend: File): Database? {
        if (backend.isFile) {
            return Json.parse(Json.read(backend), SimpleDatabase::class.java)
        }

        val databaseRepresentation =
            Json.parse(Json.read(File("${backend.path}/%database%.json")), DatabaseRepresentation::class.java)
        val database = SimpleDatabase(
            databaseRepresentation.id,
            databaseRepresentation.name,
            databaseRepresentation.type,
            databaseRepresentation.createdAt
        )

        when (databaseRepresentation.type) {
            DatabaseType.DOCUMENT -> {
                for (table in databaseRepresentation.tables) {
                    val file = File("${backend.path}/$table")
                    val node = Json.read(file)

                    when (node.get("type").asText()) {
                        "RELATIONAL" -> {
                            database.pushTable(Json.parse(node, RelationalTable::class.java))
                        }
                        "DOCUMENT" -> {
                            database.pushTable(Json.parse(node, DocumentCollection::class.java))
                        }
                    }
                }
            }
            DatabaseType.SHARDED -> {
                for (table in databaseRepresentation.tables) {
                    val file = File("${backend.path}/$table/%table%.json")
                    val tableRepresentation = Json.parse(Json.read(file), TableRepresentation::class.java)

                    val table = when (tableRepresentation.type) {
                        TableType.RELATIONAL -> RelationalTable(
                            tableRepresentation.id,
                            tableRepresentation.name,
                            database.id,
                            tableRepresentation.structure.toMutableList(),
                            tableRepresentation.primary,
                            tableRepresentation.createdAt
                        )
                        TableType.DOCUMENT -> DocumentCollection(
                            tableRepresentation.id, tableRepresentation.name, database.id, tableRepresentation.createdAt
                        )
                    }

                    for (entryFile in File("${backend.path}/$table").listFiles()) {
                        when (table.type) {
                            TableType.RELATIONAL -> table.pushEntry(
                                Json.parse(
                                    Json.read(file), RelationalEntry::class.java
                                )
                            )
                            TableType.DOCUMENT -> table.pushEntry(Json.parse(Json.read(file), Document::class.java))
                        }
                    }

                    database.pushTable(table)
                }
            }
        }

        return database
    }

    override fun loadAll() {
        File("databases").listFiles().forEach { file -> load(file)?.let { createDatabase(it) } }
    }

    override fun write(backed: File, t: Database) {
        backed.mkdirs()

        when (t.type) {
            DatabaseType.STANDALONE, DatabaseType.INTERNAL -> {
                Json.write(t, File("${backed.path}/${t.id}.json"))
            }
            DatabaseType.DOCUMENT -> {
                File("${backed.path}/${t.id}").mkdirs()
                Json.write(DatabaseRepresentation(t), File("${backed.path}/${t.id}/%database%.json"))

                t.getTables().forEach { table ->
                    Json.write(table, File("${backed.path}/${table.id}"))
                }
            }
            DatabaseType.SHARDED -> {
                File("${backed.path}/${t.id}").mkdirs()
                Json.write(DatabaseRepresentation(t), File("${backed.path}/${t.id}/%database%.json"))

                t.getTables().forEach { table ->
                    File("${backed.path}/${t.id}/${table.id}").mkdirs()
                    Json.write(TableRepresentation(table), File("${backed.path}/${t.id}/${table.id}/%table%.json"))

                    table.getEntries().forEach { entry ->
                        Json.write(entry, File("${backed.path}/${t.id}/${table.id}/${entry.id}.json"))
                    }
                }
            }
        }
    }

    override fun writeAll() {
        databases.values.forEach { database -> write(File("databases"), database) }
    }

}
