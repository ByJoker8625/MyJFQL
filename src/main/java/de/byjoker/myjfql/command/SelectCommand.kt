package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.Column
import de.byjoker.myjfql.database.Database
import de.byjoker.myjfql.database.DatabaseAction
import de.byjoker.myjfql.database.RelationalTable
import de.byjoker.myjfql.lang.ColumnComparator
import de.byjoker.myjfql.lang.ColumnFilter
import de.byjoker.myjfql.server.session.Session
import de.byjoker.myjfql.util.Order
import org.jline.reader.ParsedLine
import java.util.stream.Collectors

@CommandHandler
class SelectCommand : Command("select", mutableListOf("COMMAND", "VALUE", "FROM", "WHERE", "SORT", "ORDER", "LIMIT")) {

    override fun execute(sender: CommandSender, args: MutableMap<String, MutableList<String>>) {
        val databaseService = MyJFQL.getInstance().databaseService
        val session: Session? = sender.session

        if (session == null) {
            sender.sendError("Session of this user is invalid!")
            return
        }

        val database: Database? = session.getDatabase(databaseService)

        if (database == null) {
            sender.sendError("No database is in use for this user!")
            return
        }

        if (args.containsKey("VALUE") && args.containsKey("FROM")) {
            val from: String? = formatString(args["FROM"])

            if (from == null) {
                sender.sendError("Undefined table!")
                return
            }

            if (!database.existsTable(from)) {
                sender.sendError("Table doesn't exists!")
                return
            }

            val table = database.getTable(from)

            if (!sender.allowed(database.id, DatabaseAction.READ)) {
                sender.sendForbidden()
                return
            }

            val structure: Collection<String> = when {
                formatString(args["VALUE"]) == "*" -> table.structure
                else -> formatList(args["VALUE"]) ?: return
            }

            if (table is RelationalTable && structure.stream().anyMatch { entry -> !table.structure.contains(entry) }) {
                sender.sendError("Specified values don't match table structure!")
                return
            }

            var order: Order? = null
            var sortedBy: String? = null
            var limit = -1

            if (args.containsKey("LIMIT")) {
                limit = try {
                    formatInteger(args["LIMIT"])
                } catch (ex: Exception) {
                    sender.sendError("Unknown limit!")
                    return
                }

                if (limit <= 0) {
                    sender.sendError("Limit is too small!")
                    return
                }
            }

            if (args.containsKey("SORT")) {
                val sort = formatString(args["SORT"])

                if (sort == null) {
                    sender.sendError("Undefined sort item!")
                    return
                }

                if (!structure.contains(sort)) {
                    sender.sendError("Specified sort item doesn't match table structure!")
                    return
                }

                sortedBy = sort
            }

            if (args.containsKey("ORDER")) {
                order = try {
                    Order.valueOf(formatString(args["ORDER"])!!)
                } catch (ex: Exception) {
                    sender.sendError("Unknown sort order!")
                    return
                }

                if (sortedBy == null) sortedBy = table.primary
            }

            if (args.containsKey("PRIMARY-KEY")) {
                val primaryKey = formatString(args["PRIMARY-KEY"])

                if (primaryKey == null) {
                    sender.sendError("Undefined primary-key!")
                    return
                }

                val column = table.getColumn(primaryKey)

                if (column == null) {
                    sender.sendError("Column was not found!")
                    return
                }

                sender.sendResult(listOf(column), structure)
            } else if (args.containsKey("WHERE")) {
                val columns: List<Column>? = try {
                    ColumnFilter.filterByCommandLineArguments(
                        table, args["WHERE"], if (sortedBy == null) null else ColumnComparator(sortedBy), order
                    )
                } catch (ex: Exception) {
                    sender.sendError(ex)
                    return
                }

                if (columns == null) {
                    sender.sendError("Unknown statement error!")
                    return
                }

                if (limit != -1) {
                    sender.sendResult(columns.stream().limit(limit.toLong()).collect(Collectors.toList()), structure)
                    return
                }

                sender.sendResult(columns, structure)
            } else {
                val columns = if (sortedBy == null) table.columns else table.getColumns(
                    ColumnComparator(sortedBy), order
                )

                if (columns.isEmpty()) {
                    sender.sendResult(ArrayList<Column>(), structure)
                    return
                }

                if (limit != -1) {
                    sender.sendResult(columns.stream().limit(limit.toLong()).collect(Collectors.toList()), structure)
                    return
                }

                sender.sendResult(columns, structure)
            }

            return
        }

        sender.sendSyntax()
    }

    override fun complete(sender: CommandSender, line: ParsedLine): MutableList<String>? {
        sender.session ?: return null
        val database: Database = sender.session.getDatabase(MyJFQL.getInstance().databaseService) ?: return null

        if (!sender.allowed(database.id, DatabaseAction.READ)) {
            return null
        }

        val args = line.line().uppercase()
        val before = line.words()[line.wordIndex() - 1].uppercase()

        return when {
            !args.contains(" VALUE") -> mutableListOf("value")
            before == "VALUE" -> mutableListOf("*")
            !args.contains(" FROM") -> mutableListOf("from")
            before == "FROM" -> database.tables.map { table -> table.name }.toMutableList()
            !args.contains(" WHERE") && !args.contains(" PRIMARY-KEY") -> mutableListOf("where", "primary-key")
            before == "WHERE" || before == "PRIMARY-KEY" -> null
            !args.contains(" SORT") && !args.contains(" ORDER") && !args.contains(" LIMIT") -> mutableListOf(
                "sort",
                "order",
                "limit"
            )
            before == "SORT" -> null
            before == "ORDER" -> mutableListOf("asc", "desc")
            before == "LIMIT" -> null
            else -> null
        }
    }

}
