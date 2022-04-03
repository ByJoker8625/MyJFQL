package de.byjoker.myjfql.database

import de.byjoker.myjfql.lang.Requirement

interface Table {

    fun pushEntry(entry: Entry)
    fun getEntry(entryId: String): Entry?
    fun removeEntry(entryId: String)
    fun clear()
    fun findEntries(conditions: List<List<Requirement>>, limit: Int): List<Entry>
    fun getEntries(): List<Entry>
    fun format(type: TableType) : Table
    fun setPrimary(primary: String)
    fun getPrimary(): String
    fun setPartitioner(partitioner: String)
    fun getPartitioner(): String
    fun setStructure(structure: List<String>)
    fun getStructure(): List<String>
    fun setName(name: String)
    fun getName(): String
    fun getType(): TableType
    fun getId(): String


}
