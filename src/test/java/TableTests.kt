import de.byjoker.myjfql.database.Document
import de.byjoker.myjfql.database.RelationalTable
import de.byjoker.myjfql.database.RelationalTableEntry
import org.junit.Test
import kotlin.test.assertEquals

class TableTests {

    private val table = RelationalTable(
        "testing_table",
        mutableListOf("id"),
        "id"
    )


    @Test
    fun `table entry primary key pass relational table test`() {
        table.clear()

        /**
         * Only entries with primary key containing should pass
         */

        table.addEntry(RelationalTableEntry())

        assertEquals(table.entries.size, 0)

        table.addEntry(
            RelationalTableEntry(
                mutableMapOf("id" to "primary key value"),
                System.currentTimeMillis()
            )
        )

        assertEquals(table.entries.size, 1)
    }

    @Test
    fun `entry type pass relational table test`() {
        table.clear()

        /**
         * Only entries of type RelationalTableEntry should pass
         */

        table.addEntry(
            Document(
                mutableMapOf("_id" to "unique id of document", "id" to "primary key value"),
                System.currentTimeMillis()
            )
        )

        assertEquals(table.entries.size, 0)

        table.addEntry(
            RelationalTableEntry(
                mutableMapOf("id" to "primary key value"),
                System.currentTimeMillis()
            )
        )

        assertEquals(table.entries.size, 1)
    }


}
