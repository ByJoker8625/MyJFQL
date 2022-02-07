package de.byjoker.myjfql.database

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.exception.FileException
import de.byjoker.myjfql.util.FileFactory
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.File

class DatabaseServiceImpl() : DatabaseService {

    private val factory: FileFactory = FileFactory()
    private val databases: MutableMap<String, Database>

    init {
        databases = HashMap()
    }

    override fun createDatabase(database: Database) {
        if (getDatabaseByName(database.name) != null) throw FileException("Database already exists!")
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
        } else {
            FileUtils.deleteDirectory(File("database/$id"))
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

    override fun loadAll(space: File) {
        databases.clear()

        val files: Array<File> = space.listFiles() ?: return

        for (file in files) {
            val database: Database? = if (file.isDirectory) {
                loadDatabase(DatabaseType.SPLIT_STORAGE_TARGET, file)
            } else {
                loadDatabase(DatabaseType.SINGLE_STORAGE_TARGET, file)
            }

            if (database != null) {
                databases[database.id] = database
            }
        }
    }

    override fun loadDatabase(type: DatabaseType, file: File): Database? {
        if (!file.exists()) {
            return null;
        }

        fun loadTable(json: JSONObject): Table? {
            val name = json.getString("name")
            val columns = json.getJSONArray("columns")

            if (name.contains("%") || name.contains("#") || name.contains("'")) {
                MyJFQL.getInstance().console.logWarning("Database used unauthorized characters in the identifier!")
                return null
            }

            /**
             * If tables have not yet been created with the latest version, but are loaded with this version,
             * the type is set to 'RELATIONAL' by default to prevent errors
             */

            if (!json.has("type")) {
                json.put("type", "RELATIONAL")
            }

            when (json.getString("type")) {
                TableType.DOCUMENT.name -> {
                    val table = DocumentCollection(
                        name,
                        ArrayList(json.getJSONArray("structure").toMutableList().map { o -> o.toString() })
                    )

                    for (i in 0 until columns.length()) {
                        val column: JSONObject = columns.getJSONObject(i)

                        table.addColumn(
                            Document(
                                column.getJSONObject("content").toMap(),
                                column.getLong("creation")
                            )
                        )
                    }

                    return table
                }
                else -> {
                    val table = RelationalTable(
                        name,
                        ArrayList(json.getJSONArray("structure").toMutableList().map { o -> o.toString() }),
                        json.getString("primary")
                    )

                    for (i in 0 until columns.length()) {
                        val column: JSONObject = columns.getJSONObject(i)

                        table.addColumn(
                            RelationalColumn(
                                column.getJSONObject("content").toMap(),
                                column.getLong("creation")
                            )
                        )
                    }

                    return table
                }
            }
        }

        when (type) {
            DatabaseType.SPLIT_STORAGE_TARGET -> {
                val json = factory.load(File("${file.path}/%database%.json"))
                val database = DatabaseImpl(
                    if (json.has("id")) json.getString("id") else file.name.replace(
                        ".json".toRegex(),
                        ""
                    ),
                    json.getString("name"),
                    DatabaseType.SPLIT_STORAGE_TARGET
                )

                for (table in json.getJSONArray("tables").toList().map { any -> any.toString() }) {
                    database.saveTable(loadTable(factory.load(File("${file.path}/${table}.json"))) ?: continue)
                }

                if (database.name.contains("%") || database.name.contains("#") || database.name.contains("'")
                    || database.id.contains("%") || database.id.contains("#") || database.id.contains("'")
                ) {
                    MyJFQL.getInstance().console.logWarning("Database used unauthorized characters in the identifier!")
                    return null
                }

                return database
            }
            else -> {
                val json = factory.load(file)
                val tables = json.getJSONArray("tables")

                val database = DatabaseImpl(
                    if (json.has("id")) json.getString("id") else file.name.replace(
                        ".json".toRegex(),
                        ""
                    ),
                    json.getString("name"),
                    DatabaseType.SINGLE_STORAGE_TARGET
                )

                for (i in 0 until tables.length()) {
                    database.saveTable(loadTable(tables.getJSONObject(i)) ?: continue)
                }

                if (database.name.contains("%") || database.name.contains("#") || database.name.contains("'")
                    || database.id.contains("%") || database.id.contains("#") || database.id.contains("'")
                ) {
                    MyJFQL.getInstance().console.logWarning("Database used unauthorized characters in the identifier!")
                    return null
                }

                return database
            }
        }
    }

    override fun updateAll() {
        updateAll(File("database"))
    }

    override fun updateAll(space: File) {
        for (database in databases.values) {
            when (database.type) {
                DatabaseType.SPLIT_STORAGE_TARGET -> {
                    val folder = File("${space.path}/${database.id}")
                    folder.mkdirs()

                    val json = JSONObject()
                    json.put("id", database.id)
                    json.put("name", database.name)
                    json.put("tables", database.tables.map { table -> table.name })
                    json.put("type", database.type)
                    factory.save(File("${folder.path}/%database%.json"), json)

                    for (table in database.tables) factory.save(
                        File("${folder.path}/${table.name}.json"),
                        JSONObject(table)
                    )
                }
                else -> {
                    val file = File("${space.path}/${database.id}.json")
                    val json = JSONObject()

                    json.put("id", database.id)
                    json.put("name", database.name)
                    json.put("tables", database.tables)
                    json.put("type", database.type)

                    factory.save(file, json)
                }
            }
        }
    }

}
