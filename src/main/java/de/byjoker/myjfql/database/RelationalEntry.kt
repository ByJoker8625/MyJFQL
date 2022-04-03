package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import de.byjoker.myjfql.util.IDGenerator

@Suppress("DEPRECATION")
class RelationalEntry(
    override var id: String = IDGenerator.generateString(12),
) : EntryMatcher() {

    override var content: ObjectNode = JsonNodeFactory.instance.objectNode()

    override fun insert(field: String, value: JsonNode): Entry {
        content.put(field, JsonNodeFactory.instance.textNode(value.asText()))
        return this
    }

    override fun select(field: String): JsonNode? {
        return content.get(field)
    }

    override fun selectStringify(field: String): String? {
        if (!contains(field)) {
            return null
        }

        return content.get(field).asText(null)
    }

    override fun remove(field: String) {
        content.remove(field)
    }

    override fun contains(field: String): Boolean {
        return content.has(field)
    }

    override fun containsOrNotNullItem(field: String): Boolean {
        return !contains(field) && content.get(field).asText() != "null"
    }

    override fun applyContent(content: ObjectNode, fully: Boolean) {
        if (fully) {
            this.content = content
            return
        }

        content.fields().forEach { insert(it.key, it.value) }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RelationalEntry

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "RelationalEntry(id='$id', content=$content)"
    }


}
