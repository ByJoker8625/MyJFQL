package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import java.time.LocalDate

interface Database {

    var id: String
    var name: String
    var type: DatabaseType
    fun pushTable(table: Table)
    fun getTable(tableId: String): Table?
    fun getTableByName(name: String): Table?
    fun deleteTable(tableId: String)
    fun getTables(): List<Table>


    @get:JsonSerialize(using = LocalDateSerializer::class)
    @get:JsonDeserialize(using = LocalDateDeserializer::class)
    var createdAt: LocalDate


}
