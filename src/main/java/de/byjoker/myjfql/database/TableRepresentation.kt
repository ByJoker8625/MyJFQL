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
        table.getId(),
        table.getName(),
        table.getPrimary(),
        table.getPartitioner(),
        table.getStructure(),
        table.getType()
    )

}
