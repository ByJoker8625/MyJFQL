package de.byjoker.myjfql.database

enum class DatabaseType(val identifier: List<String>) {

    INTERNAL(listOf("internal", "intern")),
    STANDALONE(listOf("standalone", "singleton")),
    SHARDED(listOf("sharded", "shared")),
    DOCUMENT(listOf("document"));
}
