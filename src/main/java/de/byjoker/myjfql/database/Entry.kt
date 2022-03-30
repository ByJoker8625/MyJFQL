package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

interface Entry {

    var uniqueId: String
    var tableId: String
    fun remove(field: String)
    fun insert(field: String, value: JsonNode)
    fun select(field: String): Any?
    fun selectStringify(field: String): String?
    fun applyContent(content: JsonNode)
    var content: JsonNode
    var createdAt: LocalDate

}
