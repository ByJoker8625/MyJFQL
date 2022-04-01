package de.byjoker.myjfql.database

enum class TableType(val identifier: List<String>) {
    RELATIONAL(listOf("relational")), DOCUMENT(listOf("document", "non-relational"));

    companion object {

    }

}
