package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import java.time.LocalDate

data class DatabaseRepresentation(
    val id: String, val name: String, val tables: List<String>, val type: DatabaseType,
    @get:JsonSerialize(using = LocalDateSerializer::class)
    @get:JsonDeserialize(using = LocalDateDeserializer::class)
    val createdAt: LocalDate
) {

    constructor(database: Database) : this(
        database.id,
        database.name,
        database.getTables().map { table -> table.name },
        database.type,
        database.createdAt
    )

}
