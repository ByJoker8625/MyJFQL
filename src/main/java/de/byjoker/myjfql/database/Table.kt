package de.byjoker.myjfql.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import de.byjoker.myjfql.lang.Requirement
import java.time.LocalDate

interface Table {

    val id: String
    var name: String

    val structure: List<String>
    val primary: String
    val type: TableType
    fun pushEntry(entry: Entry)
    fun getEntry(entryId: String): Entry?
    fun removeEntry(entryId: String)
    fun findEntries(conditions: List<List<Requirement>>, limit: Int): List<Entry>
    fun getEntries(): List<Entry>
    fun clear()

    @get:JsonSerialize(using = LocalDateSerializer::class)
    @get:JsonDeserialize(using = LocalDateDeserializer::class)
    val createdAt: LocalDate

    @get:JsonIgnore
    val databaseId: String

}
