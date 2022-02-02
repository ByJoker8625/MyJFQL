package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.*
import de.byjoker.myjfql.lang.ColumnFilter
import org.json.JSONObject

@CommandHandler
class InsertCommand :
    Command("insert", mutableListOf("COMMAND", "INTO", "CONTENT", "KEY", "VALUE", "PRIMARY-KEY", "WHERE", "FULLY")) {

    override fun handleCommand(sender: CommandSender, args: MutableMap<String, MutableList<String>>) {
        val databaseService = MyJFQL.getInstance().databaseService
        val session = sender.session

        if (session == null) {
            sender.sendError("Session of this user is invalid!")
            return
        }

        val database = session.getDatabase(databaseService)

        if (database == null) {
            sender.sendError("No database is in use for this user!")
            return
        }

        if (!sender.allowed(database.id, DatabaseAction.READ_WRITE)) {
            sender.sendForbidden()
            return
        }

        if (!args.containsKey("INTO")) {
            sender.sendSyntax()
            return
        }

        val into: String? = formatString(args["INTO"])

        if (into == null) {
            sender.sendError("Undefined table!")
            return
        }

        if (!database.existsTable(into)) {
            sender.sendError("Table doesn't exist!")
            return
        }

        val table = database.getTable(into)
        val content: Map<String, Any>?

        when (table.type) {
            TableType.DOCUMENT -> {
                if (!args.containsKey("CONTENT")) {
                    sender.sendSyntax()
                    return
                }

                val raw: String? = formatString(args["CONTENT"])

                if (raw == null) {
                    sender.sendError("Undefined content!")
                    return
                }

                val json: JSONObject = try {
                    JSONObject(raw)
                } catch (ex: Exception) {
                    sender.sendError("Invalid json format: ${ex.message}")
                    return
                }

                content = json.toMap()
            }
            else -> {
                if (!args.containsKey("KEY") && !args.containsKey("VALUE")) {
                    sender.sendSyntax()
                    return
                }

                val keys: MutableList<String>? = formatList(args["KEY"])

                if (keys == null) {
                    sender.sendError("Undefined keys!")
                    return
                }

                val values: MutableList<String>? = formatList(args["VALUE"])

                if (values == null) {
                    sender.sendError("Undefined values!")
                    return
                }

                if (keys.size != values.size) {
                    sender.sendError("Enter suitable keys and values!")
                    return
                }

                content = HashMap()

                for (key in keys) {
                    if (!table.structure.contains(key)) {
                        sender.sendError("Specified keys don't match table structure!")
                        return
                    }

                    content.put(key, key)
                }
            }
        }

        if (content == null) {
            sender.sendError("Undefined content!")
            return
        }

        when {
            args.containsKey("WHERE") -> {
                val columns: MutableList<Column>? = try {
                    ColumnFilter.filterByCommandLineArguments(table, args["WHERE"])
                } catch (ex: Exception) {
                    sender.sendError(ex)
                    return
                }

                if (columns == null) {
                    sender.sendError("Unknown statement error!")
                    return
                }

                if (content.containsKey(table.primary) && table is DocumentTable) {
                    sender.sendError("Can't modify unique id of column!")
                    return
                }

                for (column in columns) {
                    /**
                     * To prevent duplication of an entry when the primary key is changed, the previous entry is removed
                     */

                    if (content.containsKey(table.primary)) {
                        table.removeColumn(column.selectStringify(table.primary))
                    }

                    if (args.containsKey("FULLY")) {
                        column.content = content
                    } else {
                        column.applyContent(content)
                    }
                }

                sender.sendSuccess()

                database.saveTable(table)
                databaseService.saveDatabase(database)
                return
            }
            else -> {
                val primary: String?

                if (args.containsKey("PRIMARY-KEY")) {
                    primary = formatString(args["PRIMARY-KEY"])

                    if (primary == null) {
                        sender.sendError("Undefined primary key!")
                        return
                    }
                } else {
                    primary = content[table.primary]?.toString()

                    if (table is RelationalTable && primary == null) {
                        sender.sendError("No primary key in form of unique identifier specified!")
                        return
                    }
                }

                val column: Column = table.getColumn(primary) ?: when (table.type) {
                    TableType.DOCUMENT -> DocumentColumn()
                    TableType.KEY_VALUE -> KeyValueColumn()
                    else -> RelationalColumn()
                }

                if (content.containsKey(table.primary) && table is DocumentTable) {
                    sender.sendError("Can't modify unique id of column!")
                    return
                }

                /**
                 * To prevent duplication of an entry when the primary key is changed, the previous entry is removed
                 */

                if (content.contains(table.primary)) {
                    table.removeColumn(column.selectStringify(table.primary))
                    return
                }

                if (args.containsKey("FULLY")) {
                    column.content = content
                } else {
                    column.applyContent(content)
                }

                /**
                 * If the primary key was not specified in the content but only as an additional
                 * argument and the entry does not yet exist, it will be added later
                 */

                if (!column.contains(table.primary)) {
                    column.insert(table.primary, primary)
                }

                sender.sendSuccess()

                table.addColumn(column)
                database.saveTable(table)
                databaseService.saveDatabase(database)
                return
            }
        }
    }

}
