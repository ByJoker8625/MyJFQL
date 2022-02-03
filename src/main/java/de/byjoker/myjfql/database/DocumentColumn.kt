package de.byjoker.myjfql.database

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import de.byjoker.myjfql.util.IDGenerator
import de.byjoker.myjfql.util.JsonColumnParser
import org.json.JSONPropertyIgnore
import org.json.JSONPropertyName

class DocumentColumn : ColumnMatcher {

    private var content: MutableMap<String, Any>
    private var createdAt: Long
    private var json: String

    constructor() {
        this.content = mutableMapOf("_id" to IDGenerator.generateMixed(32))
        this.createdAt = System.currentTimeMillis()
        this.json = "{}"
    }

    constructor(content: MutableMap<String, Any>, createdAt: Long) {
        if (!content.containsKey("_id")) {
            throw NullPointerException("No unique id in column content present!")
        }

        this.content = content
        this.createdAt = createdAt
        this.json = "{}"
    }

    constructor(column: Column) {
        if (!column.contains("_id")) {
            column.insert("_id", IDGenerator.generateMixed(32))
        }

        this.content = column.content
        this.createdAt = column.createdAt
        this.json = "{}"
    }

    override fun select(key: String): Any? {
        return content[key]
    }

    override fun selectStringify(key: String): String {
        return select(key).toString()
    }

    override fun insert(key: String, value: Any?) {
        if (key == "_id") {
            throw IllegalArgumentException("Can't modify unique id of column!")
        }

        content[key] = value ?: "null"
    }

    override fun remove(key: String) {
        content.remove(key)
    }

    override fun compile() {
        json = JsonColumnParser.stringify(this)
    }

    override fun contains(key: String): Boolean {
        return content.containsKey(key)
    }

    override fun containsOrNotNullItem(key: String): Boolean {
        return content.containsKey(key) && content[key] != "null"
    }

    @JsonIgnore
    @JSONPropertyIgnore
    override fun json(): String {
        return json
    }

    override fun getContent(): MutableMap<String, Any> {
        return content
    }

    override fun setContent(content: MutableMap<String, Any>) {
        content["_id"] = this.content["_id"].toString()
        this.content = content
    }

    override fun applyContent(content: MutableMap<String, Any>) {
        content.remove("_id")
        this.content.putAll(content)
    }

    fun setRawContent(content: MutableMap<String, Any>) {
        this.content = content
    }

    @JsonGetter(value = "creation")
    @JSONPropertyName("creation")
    override fun getCreatedAt(): Long {
        return createdAt
    }

    override fun setCreatedAt(createdAt: Long) {
        this.createdAt = createdAt
    }

}
