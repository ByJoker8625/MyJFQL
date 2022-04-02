package de.byjoker.myjfql.database

enum class TableType(val identifiers: List<String>) {
    RELATIONAL(listOf("relational")), DOCUMENT(listOf("document", "non-relational"));

    companion object {
        fun getTableTypeByIdentifier(identifier: String): TableType? {
            return values().firstOrNull { tableType ->
                tableType.identifiers.any { idenf ->
                    idenf.equals(
                        identifier, ignoreCase = true
                    )
                }
            }
        }
    }

}
