package de.byjoker.myjfql.database

class KeyValueTable(name: String) : RelationalTable(name, mutableListOf("key", "value"), "key") {

    override fun setPrimary(primary: String) {
        throw IllegalArgumentException("Can't modify primary key of an key value table!")
    }

    override fun setStructure(structure: Collection<String>) {
        throw IllegalArgumentException("Can't modify structure of an key value table!")
    }

    override fun reformat(type: TableType, parameters: Array<String>): Table {
        when (type) {
            TableType.DOCUMENT -> {
                val table = DocumentTable(name, ArrayList(mutableListOf("key", "value")))
                columns.forEach { column -> table.addColumn(DocumentColumn(column)) }

                return table
            }
            TableType.RELATIONAL -> {
                val table = RelationalTable(name, ArrayList(mutableListOf("key", "value")), "key")
                columns.forEach { column -> table.addColumn(RelationalColumn(column)) }

                return table
            }
            else -> {
                return this
            }
        }
    }

    override fun getType(): TableType {
        return TableType.KEY_VALUE
    }

}
