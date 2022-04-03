package de.byjoker.myjfql.database

data class DatabaseRepresentation(
    val id: String, val name: String, val tables: List<String>, val type: DatabaseType,
) {

    constructor(database: Database) : this(
        database.getId(),
        database.getName(),
        database.getTables().map { table -> table.getId() },
        database.getType()
    )

}
