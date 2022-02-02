import de.byjoker.myjfql.database.*
import de.byjoker.myjfql.lang.ColumnFilter
import de.byjoker.myjfql.lang.Requirement
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TableTests {

    private val table = RelationalTable("testing_table", mutableListOf("id", "name", "email"), "id")
    private val random = Random()


    @Test
    fun `column primary key pass relational table test`() {
        table.clear()

        /**
         * Only columns with primary key containing should pass
         */

        table.addColumn(RelationalColumn())

        assertEquals(table.columns.size, 0)

        table.addColumn(KeyValueColumn())

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
            KeyValueColumn(
                "first field value", "second field value"
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

    @Test
    fun `complex command line column filter statement test`() {
        table.clear()

        /**
         * Only required columns (random generated) should be returned
         */

        val amount = random.nextInt(100)

        for (i in 0..amount) {
            table.addColumn(
                RelationalColumn(
                    mutableMapOf(
                        "id" to i,
                        "name" to random.ints(),
                        "email" to "${random.ints()}@test"
                    ), System.currentTimeMillis()
                )
            )
        }

        assertEquals(table.columns.size - 1, amount)

        val conditions: MutableList<MutableList<Requirement>> = ArrayList()
        val shouldContain: MutableList<Int> = ArrayList()

        for (i in 0..amount / 2) {
            if (shouldContain.contains(i))
                continue

            shouldContain.add(i)
            conditions.add(
                mutableListOf(
                    Requirement(
                        "id",
                        Requirement.State.IS,
                        Requirement.Method.EQUALS,
                        i.toString()
                    )
                )
            )
        }

        val columns: List<Column>? = ColumnFilter.filter(
            table, conditions
        )

        assertNotNull(columns)
        assertEquals(columns.size, shouldContain.size)
    }

}
