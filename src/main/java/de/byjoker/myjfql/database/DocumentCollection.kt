package de.byjoker.myjfql.database

import de.byjoker.myjfql.exception.TableException
import de.byjoker.myjfql.lang.TableEntryComparator
import de.byjoker.myjfql.util.IDGenerator
import de.byjoker.myjfql.util.Order
import java.util.Collections.reverse

class DocumentCollection : Table {

    private var id: String
    private var name: String
    private var entries: MutableMap<String, TableEntry>
    private var prototypeStructure: Collection<String>

    constructor(id: String, name: String, structure: Collection<String>) {
        this.id = id
        this.name = name
        this.entries = mutableMapOf()
        this.prototypeStructure = structure
    }

    constructor(name: String, structure: Collection<String>) {
        this.id = IDGenerator.generateMixed(16)
        this.name = name
        this.entries = mutableMapOf()
        this.prototypeStructure = structure
    }

    override fun addEntry(tableEntry: TableEntry) {
        if (tableEntry !is Document) {
            throw TableException("A document table can only contain document entries!")
        }

        entries[tableEntry.selectStringify("_id")] = tableEntry
    }

    override fun removeEntry(identifier: String) {
        entries.remove(identifier)
    }

    override fun getEntry(identifier: String?): TableEntry? {
        return entries[identifier]
    }

    override fun getEntries(): Collection<TableEntry> {
        return entries.values
    }

    override fun getEntries(comparator: TableEntryComparator, order: Order): List<TableEntry> {
        val entries: List<TableEntry> = ArrayList(
            this.entries.values
        )
        entries.sortedWith(comparator)

        if (order == Order.DESC) reverse(entries)

        return entries
    }

    override fun getStructure(): Collection<String> {
        return prototypeStructure
    }

    override fun setStructure(structure: Collection<String>) {
        this.prototypeStructure = structure
    }

    override fun getPrimary(): String {
        return "_id"
    }

    override fun setPrimary(primary: String) {
        throw TableException("A document table has a fixed and not changeable primary key ('_id')!")
    }

    override fun reformat(type: TableType): Table {
        if (type == TableType.DOCUMENT) {
            return this
        }

        throw TableException("A non-relational table cannot be formatted into a rational table, since it is not possible to relationalize all values!")
    }

    override fun getType(): TableType {
        return TableType.DOCUMENT
    }

    override fun getName(): String {
        return name
    }

    override fun getId(): String {
        return id
    }

    override fun clear() {
        this.entries = HashMap()
    }

    override fun toString(): String {
        return "DocumentCollection(id='$id', name='$name', entries=$entries, prototypeStructure=$prototypeStructure)"
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


}
