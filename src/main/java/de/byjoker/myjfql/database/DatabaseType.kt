package de.byjoker.myjfql.database

enum class DatabaseType(val identifiers: List<String>) {

    INTERNAL(listOf("internal", "intern")),
    STANDALONE(listOf("standalone", "singleton")),
    SHARDED(listOf("sharded", "shared")),
    DOCUMENT(listOf("document"));

    companion object {
        fun getDatabaseTypeByIdentifier(identifier: String): DatabaseType? {
            return values().firstOrNull { databaseType ->
                databaseType.identifiers.any { idenf ->
                    idenf.equals(
                        identifier, ignoreCase = true
                    )
                }
            }
        }
    }

}
