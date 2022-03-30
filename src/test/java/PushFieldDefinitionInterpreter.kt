import java.util.*
import java.util.regex.Pattern

fun main() {
    val scanner = Scanner(System.`in`)

    fun loop() {
        try {
            println(PushFieldDefinitionInterpreter().interpret(scanner.nextLine()))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        loop()
    }

    loop()
}

data class PushFieldDefinition(val name: String, val value: Any?, val method: DefinitionMethod, val type: DataType)

enum class DefinitionMethod {
    INTERPRET, EQUALS
}

enum class DataType(val identifiers: List<String>) {
    NUMBER(listOf("number", "num")), STRING(listOf("string", "str")),
    BOOLEAN(listOf("boolean", "bool")), LIST(listOf("list", "array")),
    OBJECT(listOf("object", "obj")), NULL(listOf("null", "nothing", "undefined"));

    companion object {
        fun getDataTypeByIdentifier(identifier: String): DataType? {
            return values().firstOrNull { dataType ->
                dataType.identifiers.any { idenf ->
                    idenf.equals(
                        identifier, ignoreCase = true
                    )
                }
            }
        }
    }
}

//field = value as type and field == value (gets best data-type)

class PushFieldDefinitionInterpreter {

    fun interpret(definitions: String): List<PushFieldDefinition> {
        return definitions.split(" and ").map(::interpretDefinition).toList()
    }

    private fun interpretDefinition(definition: String): PushFieldDefinition {
        val method = if (definition.contains(" := ")) DefinitionMethod.EQUALS else DefinitionMethod.INTERPRET
        val attributes = when (method) {
            DefinitionMethod.EQUALS -> definition.split(" := ").toMutableList()
            DefinitionMethod.INTERPRET -> definition.split(" = ").toMutableList()
        }

        if (attributes.size != 2) {
            throw RuntimeException("Incomplete definition!")
        }

        attributes[1] = attributes[1].replace("'", "")

        val dataType = when {
            method == DefinitionMethod.EQUALS -> DataType.STRING
            !definition.contains(" as ") -> guessType(attributes[1])
            else -> {
                val extra = attributes[1].split(" as ")

                if (extra.size != 2) {
                    throw RuntimeException("Incomplete type definition!")
                }

                attributes[1] = extra[0]

                DataType.getDataTypeByIdentifier(extra[1]) ?: throw RuntimeException("Unknown data type!")
            }
        }

        if (!canBeType(attributes[1], dataType)) {
            throw RuntimeException("The value '${attributes[1]}' can't be an ${dataType.name}!")
        }

        return PushFieldDefinition(attributes[0].replace("'", ""), attributes[1], method, dataType)
    }

    private fun canBeType(value: String, dataType: DataType): Boolean {
        return when (dataType) {
            DataType.OBJECT, DataType.LIST, DataType.BOOLEAN, DataType.NUMBER, DataType.NULL -> guessType(value) == dataType
            DataType.STRING -> true
        }
    }

    private fun guessType(value: String): DataType {
        return when {
            value.startsWith("{") && value.endsWith("}") -> DataType.OBJECT
            value.startsWith("[") && value.endsWith("]") -> DataType.LIST
            Pattern.compile("\\d+").matcher(value).matches() -> DataType.NUMBER
            value == "true" || value == "false" -> DataType.BOOLEAN
            value == "null" -> DataType.NULL
            else -> DataType.STRING
        }
    }

}
