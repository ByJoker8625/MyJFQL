package de.byjoker.myjfql.database

import de.byjoker.myjfql.lang.Requirement

abstract class TableEntryMatcher : TableEntry {

    override fun matches(conditions: MutableList<MutableList<Requirement>>): Boolean {
        return conditions.stream().anyMatch { requirements: List<Requirement> ->
            passRequirements(
                requirements
            )
        }
    }

    private fun adjustState(state: Requirement.State, passed: Boolean): Boolean {
        return state == Requirement.State.NOT != passed
    }

    private fun matches(key: String, value: String, method: Requirement.Method): Boolean {
        if (!containsOrNotNullItem(key)) {
            return value == "null"
        }

        val given = selectStringify(key)

        println(given)
        println(value)
        println(method)
        println(given.contains(value))

        return when (method) {
            Requirement.Method.EQUALS -> {
                return given == value
            }
            Requirement.Method.EQUALS_IGNORE_CASE -> {
                return given.equals(value, ignoreCase = true)
            }
            Requirement.Method.CONTAINS -> {
                return given.contains(value)
            }
            Requirement.Method.CONTAINS_EQUALS_IGNORE_CASE -> {
                return given.contains(value, ignoreCase = true)
            }
            else -> {
                when {
                    value.startsWith("$|") && value.endsWith("|$") -> {
                        return given.contains(value.substring(2, value.length - 2), ignoreCase = true)
                    }
                    value.startsWith("$") && value.endsWith("$") -> {
                        return given.contains(value.substring(1, value.length - 1))
                    }
                    value.startsWith("|") && value.endsWith("|") -> {
                        return given.equals(value.substring(1, value.length - 1), ignoreCase = true)
                    }
                    else -> given == value
                }
            }
        }
    }

    private fun passRequirements(requirements: List<Requirement>): Boolean {
        for (requirement in requirements) {
            val method = requirement.filter
            val state = requirement.state
            val key = requirement.key
            val value = requirement.value

            when (key) {
                "*" -> {
                    if (!adjustState(state, content.keys.stream().anyMatch { given: String ->
                            !matches(
                                given,
                                value,
                                method
                            )
                        })) {
                        return false
                    }
                }
                "?" -> {
                    if (!adjustState(state, content.keys.stream().anyMatch { given: String ->
                            matches(
                                given,
                                value,
                                method
                            )
                        })) {
                        return false
                    }
                }
                else -> {
                    if (!adjustState(state, matches(key, value, method))) {
                        return false
                    }
                }
            }
        }
        return true
    }

}
