package de.byjoker.myjfql.database

import de.byjoker.myjfql.lang.TableEntryComparator
import de.byjoker.myjfql.util.IDGenerator
import de.byjoker.myjfql.util.Order

class RelationalTable : Table {

    private val id: String
    private val name: String
    private var entries: MutableMap<String, TableEntry>
    private var structure: Collection<String>
    private var primary: String

    constructor(name: String, structure: Collection<String>, primary: String) {
        this.id = IDGenerator.generateMixed(16)
        this.name = name
        this.entries = mutableMapOf()
        this.structure = structure
        this.primary = primary
    }

    constructor(id: String, name: String, structure: Collection<String>, primary: String) {
        this.id = id
        this.name = name
        this.entries = mutableMapOf()
        this.structure = structure
        this.primary = primary
    }


    override fun addEntry(tableEntry: TableEntry) {
        if (tableEntry !is RelationalTableEntry) {
            return
        }

        if (!tableEntry.containsOrNotNullItem(primary)) {
            return
        }

        entries[tableEntry.selectStringify(primary)] = tableEntry
    }

    override fun removeEntry(primary: String) {
        entries.remove(primary)
    }

    override fun getEntry(key: String): TableEntry? {
        return entries[key]
    }

    override fun getEntries(): Collection<TableEntry> {
        return entries.values
    }

    fun setEntries(entries: MutableMap<String, TableEntry>) {
        this.entries = entries
    }

    override fun getEntries(comparator: TableEntryComparator, order: Order): Collection<TableEntry> {
        val entries: MutableList<TableEntry> = ArrayList(entries.values)
        entries.sortWith(comparator)
        if (order == Order.DESC) entries.reverse()
        return entries
    }

    override fun getName(): String {
        return name
    }

    override fun clear() {
        entries = HashMap()
    }

    override fun getStructure(): Collection<String> {
        return structure
    }

    override fun setStructure(structure: Collection<String>) {
        this.structure = structure
        reindex()
    }

    override fun getPrimary(): String {
        return primary
    }

    override fun setPrimary(primary: String) {
        this.primary = primary
        reindex()
    }

    override fun reformat(type: TableType): Table {
        return when (type) {
            TableType.DOCUMENT -> {
                val table: Table = DocumentCollection(name, ArrayList(structure))

                entries.values.forEach { entry ->
                    table.addEntry(Document(entry))
                }

                table
            }
            else -> {
                this
            }
        }
    }

    override fun getType(): TableType {
        return TableType.RELATIONAL
    }

    override fun getId(): String {
        return id
    }

    private fun reindex() {
        val entries: Collection<TableEntry> = ArrayList(entries.values)
        this.entries.clear()
        entries.forEach { entry: TableEntry -> addEntry(entry) }
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
        return "RelationalTable(id='$id', name='$name', entries=$entries, structure=$structure, primary='$primary')"
    }

}
