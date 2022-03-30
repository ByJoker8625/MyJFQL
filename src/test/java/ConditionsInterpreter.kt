import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)

    fun loop() {
        try {
            println(StringConditionInterpreter().interpret(scanner.nextLine()))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        loop()
    }

    loop()
}

data class Requirement(val field: String, val value: String, val method: RequirementMethod, val state: RequirementState)

enum class RequirementState {
    NOT, IS
}

enum class RequirementMethod(val identifier: String) {
    EQUALS("equals"), EQUALS_IGNORE_CASE("equals_ignore_case"), CONTAINS("contains"), CONTAINS_EQUALS_IGNORE_CASE("contains_ignore_case"), INTERPRETED(
        "interpret"
    );

    companion object {
        fun getMethodByIdentifier(name: String): RequirementMethod {
            return values().firstOrNull { name.startsWith("${it.identifier}:", ignoreCase = true) }
                ?: return INTERPRETED
        }
    }
}

//name == ByJoker or name === ByJoker or name != equals:ByJoker

class StringConditionInterpreter {

    fun interpret(conditions: String): List<List<Requirement>> {
        if (conditions.isBlank()) {
            throw java.lang.RuntimeException("No conditions specified!")
        }

        return conditions.split(" or ").map { statements -> interpretStatements(statements) }.toList()
    }

    private fun interpretStatements(statement: String): List<Requirement> {
        return statement.split(" and ").map { requirement -> interpretRequirements(requirement) }.toList()
    }

    private fun interpretRequirements(requirements: String): Requirement {
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
            else -> throw java.lang.RuntimeException("No requirement method or/and state!")
        }

        if (method == null) {
            method = RequirementMethod.getMethodByIdentifier(attributes[1])
        }

        return Requirement(
            attributes[0].replace("'", ""),
            attributes[1].replace("${method.identifier}:", "")
                .replace("'", ""),
            method,
            state
        )
    }


}
