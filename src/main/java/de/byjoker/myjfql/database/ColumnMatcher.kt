package de.byjoker.myjfql.database

import de.byjoker.myjfql.lang.Requirement
import java.util.*

abstract class ColumnMatcher : Column {

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

        return when (method) {
            Requirement.Method.EQUALS -> {
                return value == given
            }
            Requirement.Method.EQUALS_IGNORE_CASE -> {
                return value.equals(given, ignoreCase = true)
            }
            Requirement.Method.CONTAINS -> {
                return value.contains(given)
            }
            Requirement.Method.CONTAINS_EQUALS_IGNORE_CASE -> {
                return value.lowercase(Locale.getDefault()).contains(given.lowercase(Locale.getDefault()))
            }
            else -> {
                when {
                    given.startsWith("$|") && given.endsWith("|$") -> {
                        return value.lowercase(Locale.getDefault())
                            .contains(given.substring(2, given.length - 2).lowercase(Locale.getDefault()))
                    }
                    given.startsWith("$") && given.endsWith("$") -> {
                        return value.contains(given.substring(1, given.length - 1))
                    }
                    given.startsWith("|") && given.endsWith("|") -> {
                        return value.equals(given.substring(1, given.length - 1), ignoreCase = true)
                    }
                    else -> value == given
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
