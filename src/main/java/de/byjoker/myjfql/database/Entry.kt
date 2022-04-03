package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import de.byjoker.myjfql.lang.Requirement

interface Entry {

    fun insert(field: String, value: JsonNode): Entry
    fun select(field: String): JsonNode?
    fun selectStringify(field: String): String?
    fun remove(field: String)
    fun contains(field: String): Boolean
    fun containsOrNotNullItem(field: String): Boolean
    fun applyContent(content: ObjectNode, fully: Boolean = false)
    fun matches(conditions: List<List<Requirement>>): Boolean
    fun setContent(content: ObjectNode)
    fun getContent(): ObjectNode
    fun getId(): String

}
