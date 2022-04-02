package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import de.byjoker.myjfql.exception.LanguageException
import de.byjoker.myjfql.util.IDGenerator
import java.time.LocalDate

class RelationalEntry(
    override var id: String = IDGenerator.generateString(12),
    override var createdAt: LocalDate = LocalDate.now()
) : EntryMatcher() {

    override var content: ObjectNode = JsonNodeFactory.instance.objectNode()

    override fun insert(field: String, value: JsonNode): Entry {
        content.put(field, value.toString())
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
        return content.hasNonNull(field)
    }

    override fun containsOrNotNullItem(field: String): Boolean {
        return !contains(field) && content.get(field).asText() != "null"
    }

    override fun applyContent(content: ObjectNode, fully: Boolean) {
        if (fully) {
            if (!content.hasNonNull("_id")) {
                throw LanguageException("Unique id of entry can't be removed!")
            }

            this.content = content
            return
        }

        content.fields().forEach { insert(it.key, it.value) }
    }

}
