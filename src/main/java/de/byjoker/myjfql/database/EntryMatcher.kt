package de.byjoker.myjfql.database

import de.byjoker.myjfql.lang.Requirement
import de.byjoker.myjfql.lang.RequirementMethod
import de.byjoker.myjfql.lang.RequirementState

abstract class EntryMatcher : Entry {

    override fun matches(conditions: List<List<Requirement>>): Boolean {
        return conditions.stream().anyMatch { requirements ->
            passRequirements(
                requirements
            )
        }
    }

    private fun adjustState(state: RequirementState, passed: Boolean): Boolean {
        return state == RequirementState.NOT != passed
    }

    private fun matches(key: String, value: String, method: RequirementMethod): Boolean {
        if (!containsOrNotNullItem(key)) {
            return value == "null"
        }

        val present = selectStringify(key) ?: return value == "null"

        return when (method) {
            RequirementMethod.EQUALS -> {
                return present == value
            }
            RequirementMethod.EQUALS_IGNORE_CASE -> {
                return present.equals(value, ignoreCase = true)
            }
            RequirementMethod.CONTAINS -> {
                return present.contains(value)
            }
            RequirementMethod.CONTAINS_EQUALS_IGNORE_CASE -> {
                return present.contains(value, ignoreCase = true)
            }
            else -> {
                when {
                    value.startsWith("$|") && value.endsWith("|$") -> {
                        return present.contains(value.substring(2, value.length - 2), ignoreCase = true)
                    }
                    value.startsWith("$") && value.endsWith("$") -> {
                        return present.contains(value.substring(1, value.length - 1))
                    }
                    value.startsWith("|") && value.endsWith("|") -> {
                        return present.equals(value.substring(1, value.length - 1), ignoreCase = true)
                    }
                    else -> present == value
                }
            }
        }
    }

    private fun passRequirements(requirements: List<Requirement>): Boolean {
        for (requirement in requirements) {
            if (!adjustState(requirement.state, matches(requirement.field, requirement.value, requirement.method))) {
                return false
            }
        }

        return true
    }

}
