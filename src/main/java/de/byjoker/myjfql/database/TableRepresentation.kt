package de.byjoker.myjfql.database

data class TableRepresentation(
    val id: String,
    val name: String,
    val structure: Collection<String>,
    val type: TableType
) {

    constructor(table: Table) : this(
        table.id,
        table.name,
        table.structure,
        table.type
    )
}
