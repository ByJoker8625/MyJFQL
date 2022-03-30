import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)

    fun loop() {
        try {
            println(PullFieldInterpreter().interpret(scanner.nextLine()))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        loop()
    }

    loop()
}

data class PullFieldDefinition(val name: String, val alias: String?)

//field_name as field_name_result alias and test (without alias)

class PullFieldInterpreter {

    fun interpret(definitions: String): List<PullFieldDefinition> {
        return definitions.split(" and ").map(::interpretDefinition).toList()
    }

    private fun interpretDefinition(definition: String): PullFieldDefinition {
        if (!definition.contains(" = ")) {
            return PullFieldDefinition(definition.replace("'", ""), null)
        }

        val attributes = definition.split(" = ")

        if (attributes.size != 2) {
            throw RuntimeException("Incomplete field definition!")
        }

        return PullFieldDefinition(attributes[0].replace("'", ""), attributes[1].replace("'", ""))
    }

}
