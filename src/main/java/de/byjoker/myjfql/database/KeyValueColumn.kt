package de.byjoker.myjfql.database

import com.fasterxml.jackson.annotation.JsonIgnore
import de.byjoker.myjfql.util.JsonColumnParser
import org.json.JSONPropertyIgnore

class KeyValueColumn : SimpleColumn {

    private var json: String

    constructor(key: String, value: Any?, createdAt: Long) : super(
        mutableMapOf("key" to key, "value" to (value ?: "null")),
        createdAt
    ) {
        this.json = "{}"
    }

    constructor(key: String, value: String) : this(key, value, System.currentTimeMillis())

    constructor(column: Column, key: String, value: String) : this(
        column.selectStringify(key),
        column.select(value),
        column.createdAt
    )

    constructor() : this("null", "null", System.currentTimeMillis())

    override fun compile() {
        this.json = JsonColumnParser.stringify(this)
    }

    @JsonIgnore
    @JSONPropertyIgnore
    override fun json(): String {
        return json
    }

}
