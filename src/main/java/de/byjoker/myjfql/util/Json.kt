package de.byjoker.myjfql.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

object Json {

    private val OBJECT_MAPPER = ObjectMapper()

    @JvmStatic
    fun stringify(any: Any): String {
        return OBJECT_MAPPER.writeValueAsString(any)
    }

    @JvmStatic
    fun read(file: File): JsonNode {
        return OBJECT_MAPPER.readTree(file)
    }

    @JvmStatic
    fun write(any: Any, file: File) {
        OBJECT_MAPPER.writeValue(file, any);
    }

    @JvmStatic
    fun parse(json: String): JsonNode {
        return OBJECT_MAPPER.readTree(json)
    }

    @JvmStatic
    fun <T> parse(json: String, clazz: Class<T>): T {
        return this.parse(parse(json), clazz)
    }

    @JvmStatic
    fun <T> parse(node: JsonNode, clazz: Class<T>): T {
        return OBJECT_MAPPER.treeToValue(node, clazz)
    }

    @JvmStatic
    fun <T> convert(node: JsonNode): T {
        return OBJECT_MAPPER.convertValue(node, object : TypeReference<T>() {}) as T
    }

}
