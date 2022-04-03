package de.byjoker.myjfql.database

data class TableRepresentation(
    val id: String,
    val name: String,
    val primary: String,
    val partitioner: String,
    val structure: List<String>,
    val type: TableType
) {

    constructor(table: Table) : this(
        table.id,
        table.name,
        table.primary,
        table.partitioner,
        table.structure,
        table.type
    )

}
