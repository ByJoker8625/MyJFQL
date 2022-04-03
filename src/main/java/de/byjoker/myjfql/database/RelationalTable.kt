package de.byjoker.myjfql.database

import de.byjoker.myjfql.exception.DatabaseException
import de.byjoker.myjfql.exception.LanguageException
import de.byjoker.myjfql.lang.Requirement
import de.byjoker.myjfql.util.IDGenerator

class RelationalTable(
    private var id: String = IDGenerator.generateString(12),
    private var name: String,
    private val database: Database,
    private var structure: MutableList<String>,
    private var primary: String = structure[0],
    private var partitioner: String = primary
) : Table {

    private val type: TableType = TableType.RELATIONAL
    private val entries: MutableMap<String, Entry> = mutableMapOf()

    override fun pushEntry(entry: Entry) {
        if (!entry.containsOrNotNullItem(primary)) {
            throw LanguageException("Entry have to contain primary key!")
        }

        println(entry.select(primary))

        entries[entry.selectStringify(primary).toString()] = entry
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

            print(entry.matches(conditions))

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
            TableType.DOCUMENT -> {
                val table = DocumentCollection(id, name, partitioner = partitioner, database)

                entries.values.forEach { entry ->
                    table.pushEntry(
                        Document(
                            entry.getId(),
                            entry.getContent()
                        )
                    )
                }

                table
            }
            TableType.RELATIONAL -> this
        }
    }

    private fun reindex() {
        val entries: Collection<Entry> = entries.values.toList()
        this.entries.clear()
        entries.forEach { entry -> pushEntry(entry) }
    }

    override fun setPrimary(primary: String) {
        if (!structure.contains(primary)) {
            throw DatabaseException("Structure of a relational table have to contain the primary key!")
        }

        this.primary = primary
        reindex()
    }

    override fun getPrimary(): String {
        return primary
    }

    override fun setPartitioner(partitioner: String) {
        if (!structure.contains(partitioner)) {
            throw DatabaseException("Structure of a relational table have to contain the partitioner!")
        }

        this.partitioner = partitioner
    }

    override fun getPartitioner(): String {
        return partitioner
    }

    override fun setStructure(structure: List<String>) {
        if (!structure.contains(partitioner) || !structure.contains(primary)) {
            throw DatabaseException("Structure of a relational table have to contain the partitioner and primary key!")
        }

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

        other as RelationalTable

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "RelationalTable(id='$id', name='$name', databaseId='${database.getId()}', structure=$structure, primary='$primary', type=$type, entries=$entries)"
    }


}
