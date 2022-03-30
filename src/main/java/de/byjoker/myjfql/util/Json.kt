package de.byjoker.myjfql.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

object Json {

    private val OBJECT_MAPPER = ObjectMapper()

    fun stringify(any: Any): String {
        return OBJECT_MAPPER.writeValueAsString(any)
    }

    fun read(file: File): JsonNode {
        return OBJECT_MAPPER.readTree(file)
    }

    fun write(any: Any, file: File) {
        OBJECT_MAPPER.writeValue(file, any);
    }

    fun parse(json: String): JsonNode {
        return OBJECT_MAPPER.readTree(json)
    }

    fun <T> parse(json: String, clazz: Class<T>): T {
        return this.parse(parse(json), clazz)
    }

    fun <T> parse(node: JsonNode, clazz: Class<T>): T {
        return OBJECT_MAPPER.treeToValue(node, clazz)
    }

    fun <T> convert(node: JsonNode): T {
        return OBJECT_MAPPER.convertValue(node, object : TypeReference<T>() {}) as T
    }

}
