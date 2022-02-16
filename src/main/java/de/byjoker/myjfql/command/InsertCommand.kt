package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.*
import de.byjoker.myjfql.lang.TableEntryFilter
import de.byjoker.myjfql.util.Json
import org.jline.reader.ParsedLine

@CommandHandler
class InsertCommand :
    Command("insert", mutableListOf("COMMAND", "INTO", "CONTENT", "KEY", "VALUE", "PRIMARY-KEY", "WHERE", "FULLY")) {

    override fun execute(sender: CommandSender, args: MutableMap<String, MutableList<String>>) {
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

        if (!sender.allowed(database.id, DatabasePermissionLevel.READ_WRITE)) {
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

        when (table!!.type) {
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

                val json = try {
                    Json.parse(raw)
                } catch (ex: Exception) {
                    sender.sendError("Invalid json format: ${ex.message}")
                    return
                }

                content = Json.convert(json)
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
                val entries: MutableList<TableEntry>? = try {
                    TableEntryFilter.filterByCommandLineArguments(table, args["WHERE"])
                } catch (ex: Exception) {
                    sender.sendError(ex)
                    return
                }

                if (entries == null) {
                    sender.sendError("Unknown statement error!")
                    return
                }

                if (content.containsKey(table.primary) && table is DocumentCollection) {
                    sender.sendError("Can't modify unique id of document entry!")
                    return
                }

                for (entry in entries) {
                    /**
                     * To prevent duplication of an entry when the primary key is changed, the previous entry is removed
                     */

                    if (content.containsKey(table.primary)) {
                        table.removeEntry(entry.selectStringify(table.primary))
                    }

                    if (args.containsKey("FULLY")) {
                        entry.content = content
                    } else {
                        entry.applyContent(content)
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

                val entry = table.getEntry(primary) ?: when (table.type) {
                    TableType.DOCUMENT -> Document()
                    else -> RelationalTableEntry()
                }

                /**
                 * To prevent duplication of an entry when the primary key is changed,
                 * the previous entry is removed
                 */

                if (content.contains(table.primary) && entry.select(table.primary) != content[table.primary]) {
                    table.removeEntry(entry.selectStringify(table.primary))
                }

                if (args.containsKey("FULLY")) {
                    entry.content = content
                } else {
                    entry.applyContent(content)
                }

                /**
                 * If the primary key was not specified in the content but only as an additional
                 * argument and the entry does not yet exist, it will be added later
                 */

                if (!entry.contains(table.primary)) {
                    entry.insert(table.primary, primary)
                }

                sender.sendSuccess()

                table.addEntry(entry)
                database.saveTable(table)
                databaseService.saveDatabase(database)
                return
            }
        }
    }

    override fun complete(sender: CommandSender, line: ParsedLine)
            : MutableList<String>? {
        sender.session ?: return null
        val database: Database = sender.session!!.getDatabase(MyJFQL.getInstance().databaseService) ?: return null

        if (!sender.allowed(database.id, DatabasePermissionLevel.READ_WRITE)) {
            return null
        }

        val args = line.line().uppercase()
        val before = line.words()[line.wordIndex() - 1].uppercase()

        return when {
            !args.contains(" INTO") -> mutableListOf("into")
            before == "INTO" -> database.tables.map { table -> table.name }.toMutableList()
            !args.contains(" CONTENT") && !args.contains(" KEY") -> mutableListOf("content", "key")
            before == "CONTENT" -> null
            before == "KEY" -> null
            args.contains(" KEY") && !args.contains(" VALUE") -> mutableListOf("value")
            before == "VALUE" -> null
            !args.contains(" WHERE") && !args.contains(" PRIMARY-KEY") -> mutableListOf("where", "primary-key")
            before == "WHERE" || before == "PRIMARY-KEY" -> null
            !args.contains(" FULLY") -> mutableListOf("fully")
            else -> null
        }
    }

}
