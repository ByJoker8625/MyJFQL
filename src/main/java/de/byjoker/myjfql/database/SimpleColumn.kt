package de.byjoker.myjfql.database

import com.fasterxml.jackson.annotation.JsonGetter
import org.json.JSONPropertyName

abstract class SimpleColumn(private var content: MutableMap<String, Any>, private var createdAt: Long) :
    ColumnMatcher() {

    override fun select(key: String): Any? {
        return content[key]
    }

    override fun selectStringify(key: String): String {
        return content[key].toString()
    }

    override fun insert(key: String, value: Any?) {
        content[key] = value?.toString() ?: "null"
    }

    override fun remove(key: String) {
        content.remove(key)
    }

    override fun contains(key: String): Boolean {
        return content.containsKey(key)
    }

    override fun containsOrNotNullItem(key: String): Boolean {
        return content.containsKey(key) && content[key] != "null"
    }

    override fun getContent(): Map<String, Any> {
        return content
    }

    override fun setContent(content: MutableMap<String, Any>) {
        this.content = content
    }

    override fun applyContent(content: MutableMap<String, Any>) {
        this.content.putAll(content)
    }

    @JsonGetter(value = "creation")
    @JSONPropertyName("creation")
    override fun getCreatedAt(): Long {
        return createdAt
    }

    override fun setCreatedAt(createdAt: Long) {
        this.createdAt = createdAt
    }

    override fun toString(): String {
        return "SimpleColumn(content=$content, createdAt=$createdAt)"
    }
}
