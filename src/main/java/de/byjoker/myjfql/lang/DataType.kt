package de.byjoker.myjfql.lang

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
