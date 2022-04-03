package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.exception.DatabaseException
import de.byjoker.myjfql.user.UsersTable
import de.byjoker.myjfql.util.Json
import java.io.File

class DatabaseServiceImpl : DatabaseService {

    private val databases: MutableMap<String, Database> = mutableMapOf()

    override fun createDatabase(database: Database) {
        if (databases.containsKey(database.getId()) || getDatabaseByName(database.getName()) != null) {
            throw DatabaseException("Database already exist!")
        }

        saveDatabase(database)
    }

    override fun saveDatabase(database: Database) {
        databases[database.getId()] = database
    }

    override fun deleteDatabase(id: String) {
        File("databases/$id").delete()
        databases.remove(id)
    }

    override fun getDatabase(id: String): Database? {
        return databases[id]
    }

    override fun getDatabaseByName(name: String): Database? {
        return databases.values.firstOrNull { database -> database.getName() == name }
    }

    override fun getDatabaseByIdentifier(identifier: String): Database? {
        if (identifier.startsWith("#")) {
            return getDatabase(identifier.replaceFirst("#", ""))
        }

        return getDatabaseByName(identifier)
    }

    override fun getDatabases(): List<Database> {
        return databases.values.toList()
    }

    override fun load(backend: File): Database? {
        if (!backend.exists()) {
            return null
        }

        val console = MyJFQL.getInstance().console
        console.info("Reading ${backend.name} from hard-drive...")

        fun buildTable(tableNode: JsonNode, database: Database): Table {
            if (database.getType() == DatabaseType.INTERNAL) {
                return when (tableNode["name"].asText()) {
                    "users" -> UsersTable()
                    else -> throw DatabaseException("Specified database ins't registred internal table!")
                }
            }

            return when (tableNode["type"].asText()) {
                "RELATIONAL" -> RelationalTable(
                    tableNode["id"].asText(),
                    tableNode["name"].asText(),
                    database,
                    tableNode["structure"].toList().map { it.asText() }.toMutableList(),
                    tableNode["primary"].asText(),
                    tableNode["partitioner"].asText(),
                )
                "DOCUMENT" -> DocumentCollection(
                    tableNode["id"].asText(),
                    tableNode["name"].asText(),
                    tableNode["partitioner"].asText(),
                    database
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
                table.pushEntry(buildEntry(entryNode, table.getType()))
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
                    val table = buildTable(tableNode, database)
                    pushEntries(tableNode["entries"] as ArrayNode, table)
                    database.saveTable(table)
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

            when (database.getType()) {
                DatabaseType.DOCUMENT -> {
                    for (tableId in tables) {
                        val tableNode = Json.read(File("${backend.path}/${tableId}.json"))
                        val table = buildTable(tableNode, database)
                        pushEntries(tableNode["entries"] as ArrayNode, table)

                        database.saveTable(table)
                    }
                }
                DatabaseType.SHARDED -> {
                    for (tableId in tables) {
                        val tableNode = Json.read(File("${backend.path}/${tableId}/%table%.json"))
                        val table = when (tableNode["type"].asText()) {
                            "RELATIONAL" -> RelationalTable(
                                tableNode["id"].asText(),
                                tableNode["name"].asText(),
                                database,
                                tableNode["structure"].toList().map { it.asText() }.toMutableList(),
                                tableNode["primary"].asText(),
                                tableNode["partitioner"].asText()
                            )
                            "DOCUMENT" -> DocumentCollection(
                                tableNode["id"].asText(),
                                tableNode["name"].asText(),
                                tableNode["partitioner"].asText(),
                                database
                            )
                            else -> throw DatabaseException("Unknown table type!")
                        }

                        for (entryFile in File("${backend.path}/${table.getId()}").listFiles()) {
                            if (entryFile.name == "%table%.json") continue
                            table.pushEntry(buildEntry(Json.read(entryFile), table.getType()))
                        }

                        database.saveTable(table)
                    }
                }
                else -> return null
            }

            console.info("Finished with ${backend.name} _/")
            return database
        } catch (ex: Exception) {
            console.error("Failed at ${backend.name} x")
        }

        return null
    }


    override fun loadAll() {
        val backend = File("databases")

        if (!backend.exists())
            backend.mkdir()


        backend.listFiles().forEach { file -> load(file)?.let { createDatabase(it) } }

    }

    override fun write(backed: File, t: Database) {
        val console = MyJFQL.getInstance().console
        console.info("Writing ${t.getName()} to hard-drive...")

        try {
            backed.mkdirs()

            when (t.getType()) {
                DatabaseType.STANDALONE, DatabaseType.INTERNAL -> {
                    Json.write(t, File("${backed.path}/${t.getId()}.json"))
                }
                DatabaseType.DOCUMENT -> {
                    File("${backed.path}/${t.getId()}").mkdirs()
                    Json.write(DatabaseRepresentation(t), File("${backed.path}/${t.getId()}/%database%.json"))

                    t.getTables().forEach { table ->
                        Json.write(table, File("${backed.path}/${t.getId()}/${table.getId()}.json"))
                    }
                }
                DatabaseType.SHARDED -> {
                    File("${backed.path}/${t.getId()}").mkdirs()
                    Json.write(DatabaseRepresentation(t), File("${backed.path}/${t.getId()}/%database%.json"))

                    t.getTables().forEach {
                        File("${backed.path}/${t.getId()}/${it.getId()}").mkdirs()
                        Json.write(
                            TableRepresentation(it),
                            File("${backed.path}/${t.getId()}/${it.getId()}/%table%.json")
                        )

                        it.getEntries().forEach { entry ->
                            Json.write(entry, File("${backed.path}/${t.getId()}/${it.getId()}/${entry.getId()}.json"))
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            console.error("Failed at ${t.getName()} x")
            return
        }

        console.info("Finished with ${t.getName()} _/")
    }

    override fun writeAll() {
        databases.values.forEach { write(File("databases"), it) }
    }

}
