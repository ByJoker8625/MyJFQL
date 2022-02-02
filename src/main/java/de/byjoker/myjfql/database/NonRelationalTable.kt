package de.byjoker.myjfql.database

import de.byjoker.myjfql.lang.ColumnComparator
import de.byjoker.myjfql.lang.SortingOrder
import java.util.Collections.reverse

class NonRelationalTable : Table {

    private var name: String
    private var columns: MutableMap<String, Column>
    private var prototypeStructure: MutableCollection<String>

    constructor(name: String, columns: MutableMap<String, Column>, structure: MutableCollection<String>) {
        this.name = name
        this.columns = columns
        this.prototypeStructure = structure
    }

    constructor(name: String, structure: MutableCollection<String>) {
        this.name = name
        this.columns = mutableMapOf()
        this.prototypeStructure = structure
    }

    override fun addColumn(column: Column) {
        if (column !is NonRelationalColumn) {
            throw ClassCastException("Can't cast " + column.javaClass.name + " to de.byjoker.myjfql.database.NonRelationalColumn!")
        }

        column.compile()
        columns[column.selectStringify("_id")] = column
    }

    override fun removeColumn(identifier: String) {
        columns.remove(identifier)
    }

    override fun getColumn(identifier: String?): Column? {
        return columns[identifier]
    }

    override fun getColumns(): MutableCollection<Column> {
        return columns.values
    }

    override fun getColumns(comparator: ColumnComparator, order: SortingOrder): List<Column> {
        val columns: List<Column> = ArrayList(
            this.columns.values
        )
        columns.sortedWith(comparator)

        if (order == SortingOrder.DESC) reverse(columns)

        return columns
    }

    override fun getStructure(): MutableCollection<String> {
        return prototypeStructure
    }

    override fun setStructure(structure: MutableCollection<String>) {
        this.prototypeStructure = structure
    }

    override fun getPrimary(): String {
        return "_id"
    }

    override fun setPrimary(primary: String) {
        throw IllegalArgumentException()
    }

    override fun reformat(type: TableType, parameters: Array<String>): Table {
        if (type == TableType.NON_RELATIONAL) {
            return this
        }

        throw ClassCastException("A non-relational table cannot be formatted into a rational table, since it is not possible to relationalize all values!")
    }

    override fun getType(): TableType {
        return TableType.NON_RELATIONAL
    }

    override fun getName(): String {
        return name
    }

    override fun clear() {
        this.columns = HashMap()
    }

    override fun toString(): String {
        return "NonRelationalTable(name='$name', columns=$columns, prototypeStructure=$prototypeStructure)"
    }

}
