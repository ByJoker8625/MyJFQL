package de.byjoker.myjfql.lang

import com.fasterxml.jackson.databind.JsonNode

interface Interpreter {

    fun interpretJsonQuery(json: JsonNode, query: String, stringify: Boolean, superior: Boolean = false): Any?
    fun interpretPullFieldDefinitions(definitions: String): List<PullFieldDefinition>
    fun interpretPushFieldDefinitions(definitions: String): List<PushFieldDefinition>
    fun interpretConditions(conditions: String): List<List<Requirement>>
    fun interpretCommand(command: String): Map<String, String>

}
