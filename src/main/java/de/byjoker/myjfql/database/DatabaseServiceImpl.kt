package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.exception.DatabaseException
import de.byjoker.myjfql.util.Json
import kotlinx.coroutines.*
import java.io.File

@DelicateCoroutinesApi
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

    override suspend fun load(backend: File): Database? {
        if (!backend.exists()) {
            return null
        }

        val console = MyJFQL.getInstance().console
        console.info("Reading ${backend.name} from hard-drive...")

        fun buildTable(tableNode: JsonNode, databaseId: String): Table {
            return when (tableNode["type"].asText()) {
                "RELATIONAL" -> RelationalTable(
                    tableNode["id"].asText(),
                    tableNode["name"].asText(),
                    databaseId,
                    tableNode["structure"].toList().map { it.asText() }.toMutableList(),
                    tableNode["primary"].asText(),
                    tableNode["partitioner"].asText(),
                )
                "DOCUMENT" -> DocumentCollection(
                    tableNode["id"].asText(),
                    tableNode["name"].asText(),
                    tableNode["partitioner"].asText(),
                    databaseId
                )
                else -> throw DatabaseException("Unknown table type!")
            }
        }

        fun buildEntry(entryNode: JsonNode, type: TableType): Entry {
            val content = entryNode["content"] as ObjectNode
            val entry: Entry = when (type) {
                TableType.DOCUMENT -> Document(entryNode["id"].asText())
                TableType.RELATIONAL -> RelationalEntry(entryNode["id"].asText())
            }
            entry.applyContent(content, fully = true)

            return entry
        }

        fun pushEntries(entriesNode: JsonNode, table: Table) {
            for (entryNode in entriesNode) {
                table.pushEntry(buildEntry(entryNode, table.type))
            }
        }

        try {
            if (backend.isFile) {
                val databaseNode = Json.read(backend)
                val database = SimpleDatabase(
                    databaseNode["id"].asText(),
                    databaseNode["name"].asText(),
                    DatabaseType.valueOf(databaseNode["type"].asText())
                )

                val tablesNode = databaseNode["tables"] as ArrayNode

                for (tableNode in tablesNode) {
                    val table = buildTable(tableNode, database.id)
                    pushEntries(tableNode["entries"] as ArrayNode, table)
                    database.pushTable(table)
                }

                return database
            }

            val databaseNode = Json.read(File("${backend.path}/%database%.json"))
            val database = SimpleDatabase(
                databaseNode["id"].asText(),
                databaseNode["name"].asText(),
                DatabaseType.valueOf(databaseNode["type"].asText())
            )

            val tables = databaseNode["tables"].toList().map { it.asText() }

            when (database.type) {
                DatabaseType.DOCUMENT -> {
                    for (tableId in tables) {
                        val tableNode = Json.read(File("${backend.path}/${tableId}.json"))
                        val table = buildTable(tableNode, database.id)
                        pushEntries(tableNode["entries"] as ArrayNode, table)

                        database.pushTable(table)
                    }
                }
                DatabaseType.SHARDED -> {
                    for (tableId in tables) {
                        val tableNode = Json.read(File("${backend.path}/${tableId}/%table%.json"))
                        val table = when (tableNode["type"].asText()) {
                            "RELATIONAL" -> RelationalTable(
                                tableNode["id"].asText(),
                                tableNode["name"].asText(),
                                database.id,
                                tableNode["structure"].toList().map { it.asText() }.toMutableList(),
                                tableNode["primary"].asText(),
                                tableNode["partitioner"].asText()
                            )
                            "DOCUMENT" -> DocumentCollection(
                                tableNode["id"].asText(),
                                tableNode["name"].asText(),
                                tableNode["partitioner"].asText(),
                                database.id
                            )
                            else -> throw DatabaseException("Unknown table type!")
                        }

                        for (entryFile in File("${backend.path}/${table.id}").listFiles()) {
                            if (entryFile.name == "%table%.json") continue
                            table.pushEntry(buildEntry(Json.read(entryFile), table.type))
                        }

                        database.pushTable(table)
                    }
                }
                else -> return null
            }

            console.info("Finished ${backend.name} ✓")
            return database
        } catch (ex: Exception) {
            console.error("Failed ${backend.name} ×")
        }

        return null
    }

    override fun loadAll() {
        GlobalScope.async {
            withContext(Dispatchers.Default) {
                File("databases").listFiles().forEach { file -> load(file)?.let { createDatabase(it) } }
            }
        }
    }

    override suspend fun write(backed: File, t: Database) {
        val console = MyJFQL.getInstance().console
        console.info("Writing ${t.name} to hard-drive...")

        try {
            backed.mkdirs()

            when (t.type) {
                DatabaseType.STANDALONE, DatabaseType.INTERNAL -> {
                    Json.write(t, File("${backed.path}/${t.id}.json"))
                }
                DatabaseType.DOCUMENT -> {
                    File("${backed.path}/${t.id}").mkdirs()
                    Json.write(DatabaseRepresentation(t), File("${backed.path}/${t.id}/%database%.json"))

                    t.getTables().forEach { table ->
                        Json.write(table, File("${backed.path}/${t.id}/${table.id}.json"))
                    }
                }
                DatabaseType.SHARDED -> {
                    File("${backed.path}/${t.id}").mkdirs()
                    Json.write(DatabaseRepresentation(t), File("${backed.path}/${t.id}/%database%.json"))

                    t.getTables().forEach {
                        File("${backed.path}/${t.id}/${it.id}").mkdirs()
                        Json.write(
                            TableRepresentation(it),
                            File("${backed.path}/${t.id}/${it.id}/%table%.json")
                        )

                        it.getEntries().forEach { entry ->
                            Json.write(entry, File("${backed.path}/${t.id}/${it.id}/${entry.id}.json"))
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            console.error("Failed ${t.name} ×")
            return
        }

        console.info("Finished ${t.name} ✓")
    }

    override fun writeAll() {
        GlobalScope.async {
            withContext(Dispatchers.Default) {
                databases.values.forEach { write(File("databases"), it) }
            }
        }
    }

}
