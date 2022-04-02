package de.byjoker.myjfql.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import de.byjoker.myjfql.lang.Requirement
import java.time.LocalDate

interface Table {

    var id: String
    var name: String

    @get:JsonIgnore
    var databaseId: String
    var type: TableType
    var primary: String
    fun pushEntry(entry: Entry)
    fun getEntry(entryId: String): Entry?
    fun removeEntry(entryId: String)
    fun findEntries(conditions: List<List<Requirement>>, limit: Int): List<Entry>
    fun getEntries(): List<Entry>
    fun clear()


    @get:JsonSerialize(using = LocalDateSerializer::class)
    @get:JsonDeserialize(using = LocalDateDeserializer::class)
    var createdAt: LocalDate

}
