package de.byjoker.myjfql.database

import de.byjoker.myjfql.exception.LanguageException
import de.byjoker.myjfql.lang.Requirement
import de.byjoker.myjfql.util.IDGenerator

class RelationalTable(
    override var id: String = IDGenerator.generateString(16),
    override var name: String,
    override val databaseId: String,
    override val structure: MutableList<String>,
    override val primary: String = structure[0],
    override var partitioner: String = primary
) : Table {

    override val type: TableType = TableType.RELATIONAL
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
        return "RelationalTable(id='$id', name='$name', databaseId='$databaseId', structure=$structure, primary='$primary', type=$type, entries=$entries)"
    }


}
