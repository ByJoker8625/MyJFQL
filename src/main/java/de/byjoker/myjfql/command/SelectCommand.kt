package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.Database
import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.database.TableType
import de.byjoker.myjfql.lang.TableEntryComparator
import de.byjoker.myjfql.lang.TableEntryFilter
import de.byjoker.myjfql.network.session.Session
import de.byjoker.myjfql.util.Order
import de.byjoker.myjfql.util.ResultType
import org.jline.reader.ParsedLine
import java.util.stream.Collectors

@CommandHandler
class SelectCommand : Command("select", listOf("COMMAND", "VALUE", "FROM", "WHERE", "SORT", "ORDER", "LIMIT")) {

    override fun execute(sender: CommandSender, args: Map<String, List<String>>) {
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

            val table = database.getTable(from)

            if (table == null) {
                sender.sendError("Table doesn't exists!")
                return
            }

            if (!sender.allowed(database.id, DatabasePermissionLevel.READ)) {
                sender.sendForbidden()
                return
            }

            val structure = when {
                formatString(args["VALUE"]) == "*" -> table.structure
                else -> formatList(args["VALUE"]) ?: return
            }

            if (table.type == TableType.RELATIONAL && structure.stream()
                    .anyMatch { entry -> !table.structure.contains(entry) }
            ) {
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

            val resultType = if (table.type == TableType.RELATIONAL) ResultType.RELATIONAL else ResultType.DOCUMENT

            if (args.containsKey("PRIMARY-KEY")) {
                val primaryKey = formatString(args["PRIMARY-KEY"])

                if (primaryKey == null) {
                    sender.sendError("Undefined primary-key!")
                    return
                }

                val entry = table.getEntry(primaryKey)

                if (entry == null) {
                    sender.sendError("Entry was not found!")
                    return
                }

                sender.sendResult(listOf(entry), structure, resultType)
            } else if (args.containsKey("WHERE")) {
                val entries = try {
                    TableEntryFilter.filterByCommandLineArguments(
                        table, args["WHERE"], if (sortedBy == null) null else TableEntryComparator(
                            sortedBy
                        ), order
                    )
                } catch (ex: Exception) {
                    sender.sendError(ex)
                    return
                }

                if (entries == null) {
                    sender.sendError("Unknown statement error!")
                    return
                }

                if (limit != -1) {
                    sender.sendResult(
                        entries.stream().limit(limit.toLong()).collect(Collectors.toList()),
                        structure,
                        resultType
                    )
                    return
                }

                sender.sendResult(entries, structure, resultType)
            } else {
                val entries = if (sortedBy == null) table.entries else table.getEntries(
                    TableEntryComparator(sortedBy), order
                )

                if (entries.isEmpty()) {
                    sender.sendResult(listOf(), structure, resultType)
                    return
                }

                if (limit != -1) {
                    sender.sendResult(
                        entries.stream().limit(limit.toLong()).collect(Collectors.toList()),
                        structure,
                        resultType
                    )
                    return
                }

                sender.sendResult(entries, structure, resultType)
            }

            return
        }

        sender.sendSyntax()
    }

    override fun complete(sender: CommandSender, line: ParsedLine): List<String>? {
        sender.session ?: return null
        val database: Database = sender.session!!.getDatabase(MyJFQL.getInstance().databaseService) ?: return null

        if (!sender.allowed(database.id, DatabasePermissionLevel.READ)) {
            return null
        }

        val args = line.line().uppercase()
        val before = line.words()[line.wordIndex() - 1].uppercase()

        return when {
            !args.contains(" VALUE") -> listOf("value")
            before == "VALUE" -> listOf("*")
            !args.contains(" FROM") -> listOf("from")
            before == "FROM" -> database.tables.map { table -> table.name }.toList()
            !args.contains(" WHERE") && !args.contains(" PRIMARY-KEY") -> listOf("where", "primary-key")
            before == "WHERE" || before == "PRIMARY-KEY" -> null
            !args.contains(" SORT") && !args.contains(" ORDER") && !args.contains(" LIMIT") -> listOf(
                "sort",
                "order",
                "limit"
            )
            before == "SORT" -> null
            before == "ORDER" -> listOf("asc", "desc")
            before == "LIMIT" -> null
            else -> null
        }
    }

}
