package de.byjoker.myjfql.lang

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
