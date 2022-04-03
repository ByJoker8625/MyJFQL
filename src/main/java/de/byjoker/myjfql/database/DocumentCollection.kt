package de.byjoker.myjfql.database

import de.byjoker.myjfql.exception.DatabaseException
import de.byjoker.myjfql.lang.Requirement
import de.byjoker.myjfql.util.IDGenerator

class DocumentCollection(
    private var id: String = IDGenerator.generateString(16),
    private var name: String,
    private var partitioner: String = "_id",
    private var database: Database
) : Table {

    private var structure: MutableList<String> = mutableListOf("_id")
    private val type: TableType = TableType.DOCUMENT
    private val entries: MutableMap<String, Entry> = mutableMapOf()

    override fun pushEntry(entry: Entry) {
        entries[entry.getId()] = entry
    }

    override fun getEntry(entryId: String): Entry? {
        return entries[entryId]
    }

    override fun removeEntry(entryId: String) {
        entries.remove(entryId)
    }

    override fun findEntries(conditions: List<List<Requirement>>, limit: Int): List<Entry> {
        val match: MutableList<Entry> = mutableListOf()
        var count = 0

        for (entry in entries.values) {
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

    override fun getEntries(): List<Entry> {
        return entries.values.toList()
    }

    override fun format(type: TableType): Table {
        return when (type) {
            TableType.DOCUMENT -> this
            TableType.RELATIONAL -> throw DatabaseException("A unstructured table can't be casted to a structured!")
        }
    }

    override fun setPrimary(primary: String) {
        throw DatabaseException("Document tables have a fixed primary key!")
    }

    override fun getPrimary(): String {
        return "_id"
    }

    override fun setPartitioner(partitioner: String) {
        this.partitioner = partitioner
    }

    override fun getPartitioner(): String {
        return partitioner
    }

    override fun setStructure(structure: List<String>) {
        this.structure = structure.toMutableList()
    }

    override fun getStructure(): List<String> {
        return structure
    }

    override fun setName(name: String) {
        this.name = name
    }

    override fun getName(): String {
        return name
    }

    override fun getType(): TableType {
        return type
    }

    override fun getId(): String {
        return id
    }

    override fun clear() {
        entries.clear()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentCollection

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "DocumentCollection(id='$id', name='$name', databaseId='${database.getId()}', primary='_id', type=$type, entries=$entries)"
    }

}
