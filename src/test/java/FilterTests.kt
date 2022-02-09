import de.byjoker.myjfql.database.RelationalTable
import de.byjoker.myjfql.database.RelationalTableEntry
import de.byjoker.myjfql.database.TableEntry
import de.byjoker.myjfql.lang.Requirement
import de.byjoker.myjfql.lang.TableEntryFilter
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FilterTests {

    private val table = RelationalTable(
        "testing_table",
        mutableListOf("id", "username", "name", "password", "email", "locked", "verified"),
        "id"
    )
    private val random = Random()

    @Test
    fun `random table entry filter test`() {
        table.clear()

        /**
         * Only required entries (random generated) should be returned
         */

        val amount = random.nextInt(100)

        for (i in 0..amount) {
            table.addEntry(
                RelationalTableEntry(
                    mutableMapOf(
                        "id" to i,
                        "name" to random.ints(),
                        "email" to "${random.ints()}@test"
                    ), System.currentTimeMillis()
                )
            )
        }

        assertEquals(table.entries.size - 1, amount)

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

        val entries: List<TableEntry>? = TableEntryFilter.filter(
            table, conditions
        )

        assertNotNull(entries)
        assertEquals(entries.size, shouldContain.size)
    }

    @Test
    fun `user filter test`() {
        table.clear()

        table.addEntry(
            RelationalTableEntry(
                mutableMapOf(
                    "id" to "2234",
                    "username" to "max29",
                    "name" to "max",
                    "password" to "paswd12345",
                    "email" to "max@tests.byjoker.de",
                    "verified" to "true",
                    "locked" to "false"
                ), System.currentTimeMillis()
            )
        )

        table.addEntry(
            RelationalTableEntry(
                mutableMapOf(
                    "id" to "9982",
                    "username" to "byjoker8625",
                    "name" to "ByJoker",
                    "password" to "|ignore_case_test|",
                    "email" to "byjoker@tests.byjoker.de",
                    "verified" to "false",
                    "locked" to "true"
                ), System.currentTimeMillis()
            )
        )

        table.addEntry(
            RelationalTableEntry(
                mutableMapOf(
                    "id" to "9924",
                    "username" to "admin",
                    "name" to "admin",
                    "password" to "admin",
                    "email" to "admin@tests.byjoker.de",
                    "verified" to "true",
                    "locked" to "false"
                ), System.currentTimeMillis()
            )
        )

        table.addEntry(
            RelationalTableEntry(
                mutableMapOf(
                    "id" to "0000",
                    "username" to "system",
                    "name" to "system",
                    "verified" to "true",
                    "locked" to "true"
                ), System.currentTimeMillis()
            )
        )

        assertEquals(table.entries.size, 4)

        /**
         * Check for the same word but different upper and lower case.
         */

        assertEquals(TableEntryFilter.filterByCommandLineArguments(table, mutableListOf("name === BYJOKER")).size, 0)

        /**
         * Checking for the same word but different upper and lower case with equals ignore case by method or amount dashes.
         */

        assertEquals(TableEntryFilter.filterByCommandLineArguments(table, mutableListOf("name == |BYJOKER|")).size, 1)

        assertEquals(
            TableEntryFilter.filterByCommandLineArguments(
                table,
                mutableListOf("password == equals_ignore_case:|ignore_case_test|")
            ).size, 1
        )
    }

}
