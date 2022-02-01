package de.byjoker.myjfql.database

class KeyValueTable(name: String) : RelationalTable(name, mutableListOf("key", "value"), "key") {

    override fun setPrimary(primary: String?) {
        throw IllegalArgumentException("Can't modify primary key of an key value table!")
    }

    override fun setStructure(structure: MutableCollection<String>?) {
        throw IllegalArgumentException("Can't modify structure of an key value table!")
    }

    override fun getType(): TableType {
        return TableType.KEY_VALUE
    }

}
