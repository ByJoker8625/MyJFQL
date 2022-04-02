package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import de.byjoker.myjfql.exception.LanguageException
import de.byjoker.myjfql.util.IDGenerator
import java.time.LocalDate

class Document(
    override var id: String = IDGenerator.generateMixed(12),
    override var tableId: String,
    override var content: ObjectNode = JsonNodeFactory.instance.objectNode(),
    override var createdAt: LocalDate
) : EntryMatcher() {

    override fun insert(field: String, value: JsonNode) {
        if (field == "_id") {
            if (!value.isTextual) {
                throw LanguageException("Unique id of entry have to be a string!")
            }

            id = value.asText()
        }

        content.put(field, value)
    }

    override fun select(field: String): JsonNode? {
        return content.get(field)
    }

    override fun selectStringify(field: String): String? {
        if (!containsOrNotNullItem(field)) {
            return null
        }

        return content.get(field).asText(null)
    }

    override fun remove(field: String) {
        if (field == "_id") {
            throw LanguageException("Unique id of entry can't be removed!")
        }

        content.remove(field)
    }

    override fun contains(field: String): Boolean {
        return content.has(field)
    }

    override fun containsOrNotNullItem(field: String): Boolean {
        return content.hasNonNull(field)
    }

    override fun applyContent(content: ObjectNode) {
        content.fields().forEach { insert(it.key, it.value) }
    }

}
