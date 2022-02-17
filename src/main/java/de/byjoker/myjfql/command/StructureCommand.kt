package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.database.DocumentCollection
import de.byjoker.myjfql.database.RelationalTable
import de.byjoker.myjfql.database.RelationalTableEntry
import de.byjoker.myjfql.util.ResultType

@CommandHandler
class StructureCommand :
    Command("structure", listOf("COMMAND", "OF", "SET", "ADD", "REMOVE", "MARK-PRIMARY", "PRIMARY-KEY")) {

    override fun execute(sender: CommandSender, args: Map<String, List<String>>) {
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

        if (!args.containsKey("OF")) {
            sender.sendSyntax()
            return
        }

        val tableIdenf = formatString(args["OF"])

        if (tableIdenf == null) {
            sender.sendError("Undefined table!")
            return
        }

        val table = database.getTable(tableIdenf)


        if (table == null) {
            sender.sendError("Table doesn't exists!")
            return
        }

        val structure = table.structure.toMutableList()
        val primary = table.primary

        if (args.size == 2) {
            val entries = structure.map { column ->
                val entry = RelationalTableEntry()
                entry.insert("name", column)

                if (column == primary) {
                    entry.insert("type", "PRIMARY_KEY")
                } else {
                    entry.insert("type", "COLUMN")
                }

                entry
            }

            sender.sendResult(entries, listOf("name", "type"), ResultType.RELATIONAL)
            return
        }

        if (args.containsKey("ADD")) {
            val columns = formatList(args["ADD"])

            if (columns == null) {
                sender.sendError("Undefined columns!")
                return
            }

            if (columns.any { column -> structure.contains(column) }) {
                sender.sendError("Columns already match table structure!")
                return
            }

            structure.addAll(columns)

            table.structure = structure
            database.saveTable(table)
            databaseService.saveDatabase(database)

            sender.sendSuccess();
            return
        }

        if (args.containsKey("REMOVE")) {
            val columns = formatList(args["REMOVE"])

            if (columns == null) {
                sender.sendError("Undefined columns!")
                return
            }

            if (table is RelationalTable && columns.any { column -> column == primary }) {
                sender.sendError("Primary key cannot be removed of the table structure!")
                return
            }

            if (columns.any { column -> !structure.contains(column) }) {
                sender.sendError("Columns doesn't match table structure!")
                return
            }

            structure.removeIf(columns::contains)

            table.structure = structure
            database.saveTable(table)
            databaseService.saveDatabase(database)

            sender.sendSuccess();
            return
        }

        if (args.containsKey("SET")) {
            val columns = formatList(args["STRUCTURE"])

            if (columns == null) {
                sender.sendError("Undefined table structure!")
                return
            }

            val primaryColumn: String? = when {
                args.containsKey("PRIMARY-KEY") -> formatString(args["PRIMARY-KEY"])
                else -> columns[0]
            }

            if (primaryColumn == null) {
                sender.sendError("Undefined primary key!")
                return
            }

            if (!columns.contains(primaryColumn)) {
                sender.sendError("Primary key does not match table structure!")
                return
            }

            table.structure = structure
            table.primary = primaryColumn
            database.saveTable(table)
            databaseService.saveDatabase(database)

            sender.sendSuccess()
            return
        }

        if (args.containsKey("MARK-PRIMARY")) {
            if (table is DocumentCollection) {
                sender.sendError("A document table has a fixed and not changeable primary key!")
                return
            }

            val column = formatString(args["MARK-PRIMARY"])

            if (column == null) {
                sender.sendError("Undefined primary key!")
                return
            }

            if (!structure.contains(column)) {
                sender.sendError("Primary key does not match table structure!")
                return
            }

            table.primary = column
            database.saveTable(table)
            databaseService.saveDatabase(database)

            sender.sendSuccess()
            return
        }


        sender.sendSyntax()
    }

}
