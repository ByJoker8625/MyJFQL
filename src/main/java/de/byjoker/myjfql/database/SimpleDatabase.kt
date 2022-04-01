package de.byjoker.myjfql.database

import de.byjoker.myjfql.util.IDGenerator
import java.time.LocalDate

class SimpleDatabase(
    override var id: String = IDGenerator.generateMixed(16),
    override var name: String,
    override var type: DatabaseType,
    override var createdAt: LocalDate = LocalDate.now()
) : Database {

    override fun pushTable(table: Table) {
        TODO("Not yet implemented")
    }

    override fun getTable(uniqueId: String): Table? {
        TODO("Not yet implemented")
    }

    override fun getTableByName(name: String): Table? {
        TODO("Not yet implemented")
    }

    override fun deleteTable(uniqueId: String) {
        TODO("Not yet implemented")
    }

    override fun getTables(): List<Table> {
        TODO("Not yet implemented")
    }

}
