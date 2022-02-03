package de.byjoker.myjfql.database

import de.byjoker.myjfql.lang.ColumnComparator
import de.byjoker.myjfql.lang.SortingOrder
import java.util.function.Consumer

open class RelationalTable(private val name: String, structure: List<String>, primary: String) : Table {
    private var columns: MutableMap<String, Column>
    private var structure: Collection<String>
    private var primary: String

    init {
        this.structure = structure
        this.primary = primary
        columns = HashMap()
    }

    override fun addColumn(column: Column) {
        if (column !is SimpleColumn) {
            return
        }

        if (!column.containsOrNotNullItem(primary)) {
            return
        }

        column.compile()
        columns[column.selectStringify(primary)] = column
    }

    override fun removeColumn(primary: String) {
        columns.remove(primary)
    }

    override fun getColumn(key: String): Column? {
        return columns[key]
    }

    override fun getColumns(): Collection<Column> {
        return columns.values
    }

    fun setColumns(columns: MutableMap<String, Column>) {
        this.columns = columns
    }

    override fun getColumns(comparator: ColumnComparator, order: SortingOrder): Collection<Column> {
        val columns: MutableList<Column> = ArrayList(columns.values)
        columns.sortWith(comparator)
        if (order == SortingOrder.DESC) columns.reverse()
        return columns
    }

    override fun getName(): String {
        return name
    }

    override fun clear() {
        columns = HashMap()
    }

    override fun getStructure(): Collection<String> {
        return structure
    }

    override fun setStructure(structure: Collection<String>) {
        this.structure = structure
        reindexColumns()
    }

    override fun getPrimary(): String {
        return primary
    }

    override fun setPrimary(primary: String) {
        this.primary = primary
        reindexColumns()
    }

    override fun reformat(type: TableType, parameters: Array<String>): Table {
        return when (type) {
            TableType.DOCUMENT -> {
                val table: Table = DocumentTable(name, ArrayList(structure))

                columns.values.forEach { column ->
                    table.addColumn(DocumentColumn(column))
                }

                table
            }
            else -> {
                this
            }
        }
    }

    override fun getType(): TableType {
        return TableType.RELATIONAL
    }

    private fun reindexColumns() {
        val columns: Collection<Column> = ArrayList(columns.values)
        this.columns.clear()
        columns.forEach(Consumer { column: Column -> addColumn(column) })
    }

}
