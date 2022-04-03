package de.byjoker.myjfql.database

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.exception.DatabaseException
import de.byjoker.myjfql.exception.LanguageException
import de.byjoker.myjfql.util.IDGenerator

@Suppress("DEPRECATION")
class Document(
    private var id: String = IDGenerator.generateMixed(12),
    private var content: ObjectNode = JsonNodeFactory.instance.objectNode()
) : EntryMatcher() {


    private val interpreter = MyJFQL.getInstance().interpreter

    override fun insert(field: String, value: JsonNode): Entry {
        if (field == "_id" || field == "$._id") {
            if (!value.isTextual) {
                throw LanguageException("Unique id of entry have to be a string!")
            }

            id = value.asText()
        }

        if (!field.contains(".")) {
            content.put(field, value)
            return this
        }

        val node = interpreter.interpretJsonQuery(content, field, superior = true)

        if (node == null) {
            throw DatabaseException("Superior field doesn't exits!")
        }

        val split = field.split(".")
        val target = split[split.size - 1]

        when {
            node.isArray -> {
                val array = node as ArrayNode
                val index = target.toInt()

                if (!array.has(index)) {
                    array.add(value)
                    return this
                }

                array.set(index, value)
            }
            node.isObject -> {
                (node as ObjectNode).put(target, value)
            }
            else -> throw LanguageException("Node has a type that it shouldn't have!")
        }

        return this
    }

    override fun select(field: String): JsonNode? {
        if (!field.contains(".")) {
            return content.get(field)
        }

        return interpreter.interpretJsonQuery(content, field)
    }

    override fun selectStringify(field: String): String? {
        val node = select(field) ?: return null
        return node.asText(null)
    }

    override fun remove(field: String) {
        if (field == "_id") {
            throw LanguageException("Unique id of entry can't be removed!")
        }

        content.remove(field)
    }

    override fun contains(field: String): Boolean {
        if (!field.contains(".")) {
            return content.hasNonNull(field)
        }

        return interpreter.interpretJsonQuery(content, field) != null
    }

    override fun containsOrNotNullItem(field: String): Boolean {
        return contains(field)
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

    override fun setContent(content: ObjectNode) {
        applyContent(content, fully = true)
    }

    override fun getContent(): ObjectNode {
        return content
    }

    override fun getId(): String {
        return id
    }


    override fun toString(): String {
        return "Document(id='$id', content=$content)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Document

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}
