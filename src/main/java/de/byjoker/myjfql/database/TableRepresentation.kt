package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import java.time.LocalDate

data class TableRepresentation(
    val id: String,
    val name: String,
    val primary: String,
    val structure: List<String>,
    val type: TableType,
    @get:JsonSerialize(using = LocalDateSerializer::class)
    @get:JsonDeserialize(using = LocalDateDeserializer::class)
    val createdAt: LocalDate
) {

    constructor(table: Table) : this(table.id, table.name, table.primary, table.structure, table.type, table.createdAt)

}
