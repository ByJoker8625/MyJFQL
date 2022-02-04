import de.byjoker.myjfql.database.DocumentColumn
import de.byjoker.myjfql.database.RelationalColumn
import de.byjoker.myjfql.database.RelationalTable
import org.junit.Test
import kotlin.test.assertEquals

class TableTests {

    private val table = RelationalTable(
        "testing_table",
        mutableListOf("id"),
        "id"
    )


    @Test
    fun `column primary key pass relational table test`() {
        table.clear()

        /**
         * Only columns with primary key containing should pass
         */

        table.addColumn(RelationalColumn())

        assertEquals(table.columns.size, 0)

        table.addColumn(
            RelationalColumn(
                mutableMapOf("id" to "primary key value") as Map<String, Any>,
                System.currentTimeMillis()
            )
        )

        assertEquals(table.columns.size, 1)
    }

    @Test
    fun `column type pass relational table test`() {
        table.clear()

        /**
         * Only columns of type RelationalColumn should pass
         */

        table.addColumn(
            DocumentColumn(
                mutableMapOf("_id" to "unique id of document column", "id" to "primary key value"),
                System.currentTimeMillis()
            )
        )

        assertEquals(table.columns.size, 0)

        table.addColumn(
            RelationalColumn(
                mutableMapOf("id" to "primary key value") as Map<String, Any>,
                System.currentTimeMillis()
            )
        )

        assertEquals(table.columns.size, 1)
    }


}
