package de.byjoker.myjfql.lang

data class PushFieldDefinition(val name: String, val value: Any?, val method: Method, val type: DataType) {

    enum class Method {
        INTERPRET, EQUALS
    }

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

}
