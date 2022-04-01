package de.byjoker.myjfql.lang

data class PushFieldDefinition(
    val name: String,
    val value: Any?,
    val method: PushFieldDefinitionMethod,
    val type: DataType
)
