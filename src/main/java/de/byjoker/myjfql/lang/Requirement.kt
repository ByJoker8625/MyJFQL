package de.byjoker.myjfql.lang

data class Requirement(val field: String, val value: String, val method: RequirementMethod, val state: RequirementState)
