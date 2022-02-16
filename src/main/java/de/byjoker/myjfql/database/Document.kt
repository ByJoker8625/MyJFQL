package de.byjoker.myjfql.database

import de.byjoker.myjfql.util.IDGenerator

class Document : TableEntryMatcher {

    private var content: MutableMap<String, Any?>
    private var createdAt: Long

    constructor() {
        this.content = mutableMapOf("_id" to IDGenerator.generateMixed(32))
        this.createdAt = System.currentTimeMillis()
    }

    constructor(content: MutableMap<String, Any?>, createdAt: Long) {
        if (!content.containsKey("_id")) {
            throw NullPointerException("No unique id in entry present!")
        }

        this.content = content
        this.createdAt = createdAt
    }

    constructor(tableEntry: TableEntry) {
        if (!tableEntry.contains("_id")) {
            tableEntry.insert("_id", IDGenerator.generateMixed(32))
        }

        this.content = tableEntry.content
        this.createdAt = tableEntry.createdAt
    }

    override fun select(key: String): Any? {
        return content[key]
    }

    override fun selectStringify(key: String): String {
        return select(key).toString()
    }

    override fun append(key: String, value: Any?): TableEntry {
        insert(key, value)
        return this
    }

    override fun insert(key: String, value: Any?) {
        content[key] = value ?: "null"
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

    override fun getContent(): MutableMap<String, Any?> {
        return content
    }

    override fun setContent(content: MutableMap<String, Any?>) {
        this.content = content
    }

    override fun applyContent(content: MutableMap<String, Any>) {
        this.content.putAll(content)
    }

    override fun getCreatedAt(): Long {
        return createdAt
    }

    override fun setCreatedAt(createdAt: Long) {
        this.createdAt = createdAt
    }

}
