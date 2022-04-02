package de.byjoker.myjfql.lang

import com.fasterxml.jackson.databind.JsonNode
import de.byjoker.myjfql.command.CommandService
import de.byjoker.myjfql.exception.CommandException
import de.byjoker.myjfql.exception.LanguageException
import java.util.*
import java.util.regex.Pattern


class JFQLInterpreter(val commandService: CommandService) : Interpreter {

    override fun interpretJsonQuery(json: JsonNode, query: String, superior: Boolean): JsonNode? {
        val fields = mutableListOf(*query.split(".").toTypedArray())

        if (fields.indexOf("$") == 0) {
            fields.removeAt(0)
        }

        if (superior && fields.isNotEmpty()) {
            fields.removeLast()
        }

        fun search(node: JsonNode, fields: List<String>, dataType: DataType): JsonNode? {
            return try {
                if (fields.isEmpty()) {
                    return node
                }

                when (dataType) {
                    DataType.LIST -> {
                        val index = fields[0].toInt()

                        val o = node[index] ?: return null

                        if (o.isObject && fields.size != 1) {
                            return search(o, fields.subList(1, fields.size), DataType.OBJECT)
                        }

                        if (o.isArray && fields.size != 1)
                            search(
                                o, fields.subList(1, fields.size), DataType.LIST
                            ) else o
                    }
                    DataType.OBJECT -> {
                        val o = node[fields[0]] ?: return null

                        if (o.isArray && fields.size != 1) {
                            return search(o, fields.subList(1, fields.size), DataType.LIST)
                        }

                        if (o.isObject) {
                            return if (fields.size != 1) {
                                search(o, fields.subList(1, fields.size), DataType.OBJECT)
                            } else o
                        }

                        if (fields.size != 1) null else o
                    }
                    else -> null
                }
            } catch (ex: Exception) {
                null
            }
        }

        return search(json, fields, DataType.OBJECT)
    }

    override fun interpretPullFieldDefinitions(definitions: String): List<PullFieldDefinition> {
        fun interpretDefinition(definition: String): PullFieldDefinition {
            if (!definition.contains(" = ")) {
                return PullFieldDefinition(definition, null)
            }

            val attributes = definition.split(" = ")

            if (attributes.size != 2) {
                throw RuntimeException("Incomplete field definition!")
            }

            return PullFieldDefinition(attributes[0].replace("'", ""), attributes[1].replace("'", ""))
        }

        return definitions.split(" and ").map(::interpretDefinition).toList()
    }

    override fun interpretPushFieldDefinitions(definitions: String): List<PushFieldDefinition> {
        fun guessType(value: String): DataType {
            return when {
                value.startsWith("{") && value.endsWith("}") -> DataType.OBJECT
                value.startsWith("[") && value.endsWith("]") -> DataType.LIST
                Pattern.compile("\\d+").matcher(value).matches() -> DataType.NUMBER
                value == "true" || value == "false" -> DataType.BOOLEAN
                value == "null" -> DataType.NULL
                else -> DataType.STRING
            }
        }

        fun canBeType(value: String, dataType: DataType): Boolean {
            return when (dataType) {
                DataType.OBJECT, DataType.LIST, DataType.BOOLEAN, DataType.NUMBER, DataType.NULL -> guessType(value) == dataType
                DataType.STRING -> true
            }
        }

        fun interpretDefinition(definition: String): PushFieldDefinition {
            val method =
                if (definition.contains(" := ")) PushFieldDefinitionMethod.EQUALS else PushFieldDefinitionMethod.INTERPRET
            val attributes = when (method) {
                PushFieldDefinitionMethod.EQUALS -> definition.split(" := ").toMutableList()
                PushFieldDefinitionMethod.INTERPRET -> definition.split(" = ").toMutableList()
            }

            if (attributes.size != 2) {
                throw RuntimeException("Incomplete definition!")
            }

            attributes[1] = attributes[1].replace("'", "")

            val dataType = when {
                method == PushFieldDefinitionMethod.EQUALS -> DataType.STRING
                !definition.contains(" as ") -> guessType(attributes[1])
                else -> {
                    val extra = attributes[1].split(" as ")

                    if (extra.size != 2) {
                        throw RuntimeException("Incomplete type definition!")
                    }

                    attributes[1] = extra[0].replace("'", "")

                    DataType.getDataTypeByIdentifier(extra[1]) ?: throw RuntimeException("Unknown data type!")
                }
            }

            if (!canBeType(attributes[1], dataType)) {
                throw RuntimeException("The value '${attributes[1]}' can't be an ${dataType.name}!")
            }

            return PushFieldDefinition(attributes[0].replace("'", ""), attributes[1], method, dataType)
        }

        return definitions.split(" and ").map(::interpretDefinition).toList()
    }

    override fun interpretConditions(conditions: String): List<List<Requirement>> {
        if (conditions.isBlank()) {
            throw LanguageException("No conditions specified!")
        }

        fun interpretRequirements(requirements: String): Requirement {
            var method: RequirementMethod? = null
            val state: RequirementState

            val attributes: List<String> = when {
                requirements.contains(" !== ") -> {
                    state = RequirementState.NOT
                    method = RequirementMethod.EQUALS
                    requirements.split(" !== ")
                }
                requirements.contains(" != ") -> {
                    state = RequirementState.NOT
                    requirements.split(" != ")
                }
                requirements.contains(" === ") -> {
                    state = RequirementState.IS
                    method = RequirementMethod.EQUALS
                    requirements.split(" === ")
                }
                requirements.contains(" == ") -> {
                    state = RequirementState.IS
                    requirements.split(" == ")
                }
                else -> throw LanguageException("No requirement method or/and state!")
            }

            if (method == null) {
                method = RequirementMethod.getMethodByIdentifier(attributes[1])
            }

            return Requirement(
                attributes[0].replace("'", ""),
                attributes[1].replace("${method.identifier}:", "").replace("'", ""),
                method,
                state
            )
        }

        fun interpretStatements(statements: String): List<Requirement> {
            return statements.split(" and ").map { requirements -> interpretRequirements(requirements) }.toList()
        }

        return conditions.split(" or ").map { statements -> interpretStatements(statements) }.toList()
    }

    override fun interpretCommand(command: String): Map<String, List<String>> {
        val attributes: List<String> = command.split(" ")

        val definitions: MutableList<String> = mutableListOf()
        var builder: StringBuilder? = null

        for (index in attributes.indices) {
            val current = attributes[index]

            if (index == 0) {
                definitions.add("command")
                definitions.add(current)
            } else {
                if (current.startsWith("'") && current.endsWith("'") && current != "'") {
                    definitions.add(current)
                } else if (current == "'" && builder == null || current.startsWith("'") && !current.endsWith("'")) {
                    builder = StringBuilder(current)
                } else if (current == "'" || !current.startsWith("'") && current.endsWith("'")) {
                    if (builder == null) {
                        definitions.add(current)
                    } else {
                        builder.append(" ").append(current)
                        definitions.add(builder.toString())
                        builder = null
                    }
                } else {
                    if (builder == null) {
                        definitions.add(current)
                    } else {
                        builder.append(" ").append(current)
                    }
                }
            }
        }

        val keywords: List<String> = try {
            commandService.getCommand(definitions[1])!!.keywords
        } catch (ex: Exception) {
            throw CommandException("Unknown command $command!")
        }

        val arguments: MutableMap<String, MutableList<String>> = mutableMapOf()
        var section: String? = null

        for (current in definitions) {
            if (keywords.contains(current)) {
                arguments[current] = mutableListOf()
                section = current
            } else {
                if (section == null) {
                    break
                }

                if (!arguments.containsKey(section)) {
                    arguments[section] = Collections.singletonList(current)
                } else {
                    arguments[section]!!.add(current)
                }
            }
        }

        return arguments
    }


}
