package de.byjoker.myjfql.database

import de.byjoker.myjfql.exception.DatabaseException
import de.byjoker.myjfql.util.IDGenerator
import java.io.File

class SimpleDatabase(
    private var id: String = IDGenerator.generateString(12),
    private var name: String,
    private var type: DatabaseType,
) : Database {

    private val tables: MutableMap<String, Table> = mutableMapOf()

    override fun createTable(table: Table) {
        if (getTable(table.getId()) != null || getTableByName(table.getName()) != null) {
            throw DatabaseException("Table already exist!")
        }

        saveTable(table)
    }

    override fun saveTable(table: Table) {
        tables[table.getId()] = table
    }

    override fun getTable(tableId: String): Table? {
        return tables[tableId]
    }

    override fun getTableByName(name: String): Table? {
        return tables.values.firstOrNull { table -> table.getName() == name }
    }

    override fun getTableByIdentifier(identifier: String): Table? {
        if (identifier.startsWith("#")) {
            return getTable(identifier.replaceFirst("#", ""))
        }

        return getTableByName(identifier)
    }

    override fun deleteTable(tableId: String) {
        if (type == DatabaseType.INTERNAL) {
            throw DatabaseException("Internal tables can't be deleted manually!")
        }

        if (type == DatabaseType.DOCUMENT || type == DatabaseType.SHARDED) {
            File("databases/${this.id}/$tableId").delete()
        }

        tables.remove(tableId)
    }

    override fun getTables(): List<Table> {
        return tables.values.toList()
    }

    override fun format(type: DatabaseType, databaseService: DatabaseService) {
        val original = this.type

        try {
            databaseService.deleteDatabase(id)
            this.type = type
            databaseService.saveDatabase(this)
        } catch (ex: Exception) {
            databaseService.deleteDatabase(id)
            this.type = original
            databaseService.saveDatabase(this)
        }
    }

    override fun setName(name: String) {
        this.name = name
    }

    override fun getType(): DatabaseType {
        return type
    }

    override fun getName(): String {
        return name
    }

    override fun getId(): String {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleDatabase

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "SimpleDatabase(id='$id', name='$name', type=$type, tables=${tables.keys}})"
    }


}
