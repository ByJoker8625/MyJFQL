package de.byjoker.myjfql.database

import de.byjoker.myjfql.lang.Requirement
import de.byjoker.myjfql.util.IDGenerator
import java.time.LocalDate

class DocumentCollection(
    override var id: String = IDGenerator.generateString(16),
    override var name: String,
    override var databaseId: String,
    override var createdAt: LocalDate = LocalDate.now()
) : Table {

    override var primary: String = "_id"
    override var type: TableType = TableType.DOCUMENT
    private val entries: MutableMap<String, Entry> = mutableMapOf()

    override fun pushEntry(entry: Entry) {
        entries[entry.id] = entry
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

    override fun clear() {
        entries.clear()
    }

}
