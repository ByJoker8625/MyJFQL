package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.JsonNode
import de.byjoker.myjfql.exception.DatabaseException
import de.byjoker.myjfql.exception.FileException
import de.byjoker.myjfql.util.Json
import java.io.File

class DatabaseServiceImpl : DatabaseService {

    private val databases: MutableMap<String, Database> = mutableMapOf()

    override fun createDatabase(database: Database) {
        if (existsDatabaseByName(database.name)) {
            throw DatabaseException("Database already exists!")
        }

        if (existsDatabase(database.id)) {
            database.regenerateId()
            createDatabase(database)
            return
        }
        saveDatabase(database)
    }

    override fun saveDatabase(database: Database) {
        databases[database.id] = database
    }

    override fun existsDatabaseByIdentifier(identifier: String): Boolean {
        return if (identifier.startsWith("#")) existsDatabase(
            identifier.replaceFirst(
                "#".toRegex(),
                ""
            )
        ) else existsDatabaseByName(identifier)
    }

    override fun existsDatabaseByName(name: String): Boolean {
        return databases.values.stream().anyMatch { database: Database -> database.name == name }
    }

    override fun existsDatabase(id: String): Boolean {
        return databases.containsKey(id)
    }

    override fun deleteDatabase(id: String) {
        val file = File("database/$id.json")

        if (file.exists()) {
            file.delete()
        }

        databases.remove(id)
    }

    override fun getDatabaseByIdentifier(identifier: String): Database? {
        return if (identifier.startsWith("#")) {
            getDatabase(identifier.replaceFirst("#".toRegex(), ""))
        } else getDatabaseByName(identifier)
    }

    override fun getDatabaseByName(name: String): Database? {
        return databases.values.stream().filter { database: Database -> database.name == name }.findFirst()
            .orElse(null)
    }

    override fun getDatabase(id: String?): Database? {
        return databases[id]
    }

    override fun getDatabases(): Collection<Database> {
        return databases.values
    }

    override fun loadAll() {
        loadAll(File("database"))
    }

    override fun loadAll(backend: File) {
        if (!backend.isDirectory) {
            throw FileException("${backend.name} isn't a valid database file space!")
        }

        databases.clear()

        val files = backend.listFiles() ?: return

        for (file in files) {
            val database: Database? = if (file.isDirectory) {
                loadDatabase(DatabaseType.SPLIT, file)
            } else {
                loadDatabase(DatabaseType.SINGLETON, file)
            }

            if (database != null) {
                databases[database.id] = database
            }
        }
    }

    private fun parseTable(node: JsonNode): Table {
        val reserved = listOf('%', '#', '\'')

        if (reserved.any { char -> node.get("name").asText().contains(char) }) {
            throw DatabaseException("Database used reserved character in id or name!")
        }

        val type = when {
            node.has("type") -> {
                TableType.valueOf(node.get("type").asText())
            }
            else -> {
                TableType.RELATIONAL
            }
        }

        val table: Table

        val structure: MutableList<String> = Json.convert(node.get("structure"))
        val entries = if (node.has("entries")) node.get("entries") else node.get("columns")

        when (type) {
            TableType.RELATIONAL -> {
                table = RelationalTable(node.get("name").asText(), structure, node.get("primary").asText())

                entries.forEach { entry ->
                    table.addEntry(
                        RelationalTableEntry(
                            Json.convert(entry.get("content")),
                            if (entry.has("createdAt")) entry.get("createdAt").asLong() else entry.get("creation")
                                .asLong()
                        )
                    )
                }
            }
            TableType.DOCUMENT -> {
                table = RelationalTable(node.get("name").asText(), structure, node.get("primary").asText())

                entries.forEach { entry ->
                    table.addEntry(
                        Document(
                            Json.convert(entry.get("content")),
                            if (entry.has("createdAt")) entry.get("createdAt").asLong() else entry.get("creation")
                                .asLong()
                        )
                    )
                }
            }
        }


        return table
    }

    private fun loadDatabase(type: DatabaseType, file: File): Database? {
        if (!file.exists()) {
            return null
        }

        val reserved = listOf('%', '#', '\'')

        when (type) {
            DatabaseType.SINGLETON -> {
                val node = Json.read(file)
                val database = SimpleDatabase(node.get("id").asText(), node.get("name").asText(), type)

                if (reserved.any { char -> database.name.contains(char) || database.id.contains(char) }) {
                    throw DatabaseException("Database used reserved character in id or name!")
                }

                node.get("tables").forEach { table ->
                    database.saveTable(parseTable(table))
                }

                return database
            }
            DatabaseType.SPLIT -> {
                val node = Json.read(File("${file.path}/%database%.json"))
                val database = SimpleDatabase(
                    node.get("id").asText(),
                    node.get("name").asText(),
                    DatabaseType.valueOf(node.get("type").asText())
                )

                if (reserved.any { char -> database.name.contains(char) || database.id.contains(char) }) {
                    throw DatabaseException("Database used reserved character in id or name!")
                }

                node.get("tables").forEach { table ->
                    database.saveTable(parseTable(Json.read(File("${file.path}/${table.asText()}.json"))))
                }

                return database
            }
            DatabaseType.INTERNAL -> {
                throw DatabaseException("System-internal tables cannot be created manually!")
            }
        }
    }

    override fun updateAll() {
        updateAll(File("database"))
    }

    override fun updateAll(backend: File) {
        for (database in databases.values) when (database.type) {
            DatabaseType.SPLIT -> {
                val folder = File("${backend.path}/${database.id}")
                folder.mkdirs()

                Json.write(DatabaseRepresentation(database), File("${folder.path}/%database%.json"))

                database.tables.forEach { table ->
                    Json.write(
                        table,
                        File("${folder.path}/${table.name}.json")
                    )
                }
            }
            DatabaseType.SINGLETON -> {
                Json.write(database, File("${backend.path}/${database.id}.json"))
            }
            DatabaseType.INTERNAL -> continue
        }
    }

}
