package de.byjoker.myjfql.database

import de.byjoker.myjfql.exception.DatabaseException
import de.byjoker.myjfql.util.IDGenerator
import java.io.File

class SimpleDatabase(
    override var id: String = IDGenerator.generateString(16),
    override var name: String,
    override var type: DatabaseType,
) : Database {

    private val tables: MutableMap<String, Table> = mutableMapOf()

    override fun pushTable(table: Table) {
        tables[table.id] = table
    }

    override fun getTable(tableId: String): Table? {
        return tables[tableId]
    }

    override fun getTableByName(name: String): Table? {
        return tables.values.firstOrNull { table -> table.name == name }
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
        return "SimpleDatabase(id='$id', name='$name', type=$type, tables=$tables)"
    }


}
