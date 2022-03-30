package de.byjoker.myjfql.lang

data class Requirement(val field: String, val value: String, val method: Method, val state: State) {

    enum class State {
        NOT, IS
    }

    enum class Method(val identifier: String) {
        EQUALS("equals"), EQUALS_IGNORE_CASE("equals_ignore_case"), CONTAINS("contains"), CONTAINS_EQUALS_IGNORE_CASE("contains_ignore_case"), INTERPRETED(
            "interpret"
        );

        companion object {
            fun getMethodByIdentifier(name: String): Method {
                return values().firstOrNull { name.startsWith("${it.identifier}:", ignoreCase = true) }
                    ?: return INTERPRETED
            }
        }
    }


}
