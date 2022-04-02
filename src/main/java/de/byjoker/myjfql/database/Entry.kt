package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import de.byjoker.myjfql.lang.Requirement
import java.time.LocalDate

interface Entry {

    val id: String
    fun insert(field: String, value: JsonNode): Entry
    fun select(field: String): JsonNode?
    fun selectStringify(field: String): String?
    fun remove(field: String)
    fun contains(field: String): Boolean
    fun containsOrNotNullItem(field: String): Boolean
    fun applyContent(content: ObjectNode, fully: Boolean = false)
    fun matches(conditions: List<List<Requirement>>): Boolean
    val content: ObjectNode

    @get:JsonSerialize(using = LocalDateSerializer::class)
    @get:JsonDeserialize(using = LocalDateDeserializer::class)
    val createdAt: LocalDate

}
