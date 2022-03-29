package de.byjoker.myjfql.database

class RelationalTableEntry(
    private var content: MutableMap<String, Any?> = HashMap(),
    private var createdAt: Long = System.currentTimeMillis()
) :
    TableEntryMatcher() {

    constructor(tableEntry: TableEntry) : this(tableEntry.content, tableEntry.createdAt)

    override fun select(key: String): Any? {
        return content[key]
    }

    override fun selectStringify(key: String): String {
        return content[key].toString()
    }

    override fun append(key: String, value: Any?): TableEntry {
        insert(key, value)
        return this
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

    override fun getContent(): Map<String, Any?> {
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


    override fun toString(): String {
        return "RelationalTableEntry(content=$content, createdAt=$createdAt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RelationalTableEntry

        if (content != other.content) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }


}
