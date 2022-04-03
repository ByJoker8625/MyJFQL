package de.byjoker.myjfql.lang

import com.fasterxml.jackson.databind.JsonNode

interface Interpreter {

    companion object {
        val NAMING_CONVENTIONS_REGEX = "[a-zA-Z0-9\\-_]".toRegex()
    }

    fun interpretJsonQuery(json: JsonNode, query: String, superior: Boolean = false): JsonNode?
    fun interpretPullFieldDefinitions(definitions: String): List<PullFieldDefinition>
    fun interpretPushFieldDefinitions(definitions: String): List<PushFieldDefinition>
    fun interpretConditions(conditions: String): List<List<Requirement>>
    fun interpretCommand(command: String): Map<String, List<String>>

}
