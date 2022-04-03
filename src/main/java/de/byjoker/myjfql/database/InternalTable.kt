package de.byjoker.myjfql.database

import de.byjoker.myjfql.exception.DatabaseException
import de.byjoker.myjfql.lang.Requirement

abstract class InternalTable(
    private val name: String,
    private val structure: List<String>,
    private val primary: String = structure[0]
) : Table {

    override fun findEntries(conditions: List<List<Requirement>>, limit: Int): List<Entry> {
        val match: MutableList<Entry> = mutableListOf()
        var count = 0

        for (entry in getEntries()) {
            if (count == limit) {
                break
            }

            if (entry.matches(conditions)) {
                match.add(entry)
                count++
            }
        }

        return match
    }

    override fun format(type: TableType): Table {
        throw DatabaseException("Internal tables are immutable!")
    }

    override fun setPrimary(primary: String) {
        throw DatabaseException("Internal tables are immutable!")
    }

    override fun getPrimary(): String {
        return primary
    }

    override fun setPartitioner(partitioner: String) {
        throw DatabaseException("Internal tables are immutable!")
    }

    override fun getPartitioner(): String {
        return primary
    }

    override fun setStructure(structure: List<String>) {
        throw DatabaseException("Internal tables are immutable!")
    }

    override fun getStructure(): List<String> {
        return structure
    }

    override fun setName(name: String) {
        throw DatabaseException("Internal tables are immutable!")
    }

    override fun getName(): String {
        return name
    }

    override fun getType(): TableType {
        return TableType.RELATIONAL
    }

    override fun getId(): String {
        return name
    }
}
