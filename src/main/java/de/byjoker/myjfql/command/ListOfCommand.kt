package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.Database
import de.byjoker.myjfql.database.DatabaseActionPerformType
import de.byjoker.myjfql.database.RelationalTableEntry
import de.byjoker.myjfql.database.TableEntry
import de.byjoker.myjfql.util.ResultType
import org.jline.reader.ParsedLine

@CommandHandler
class ListOfCommand : ConsoleCommand("listof", mutableListOf("COMMAND", "TABLES", "DATABASES")) {

    override fun executeAsConsole(sender: ConsoleCommandSender, args: MutableMap<String, MutableList<String>>) {
        val databaseService = MyJFQL.getInstance().databaseService

        if (args.containsKey("DATABASES")) {
            sender.sendResult(databaseService.databases.filter { database ->
                sender.allowed(
                    database.id,
                    DatabaseActionPerformType.READ
                )
            }.map { database ->
                RelationalTableEntry().append("name", database.name).append("type", database.type)
            }.toMutableList() as Collection<TableEntry>, mutableListOf("name", "type"), ResultType.LEGACY)
            return
        }

        if (args.containsKey("TABLES")) {
            val database: Database? = sender.session.getDatabase(databaseService)

            if (database == null) {
                sender.sendError("No database is in use for this user!")
                return
            }

            if (!sender.allowed(database.id, DatabaseActionPerformType.READ_WRITE)) {
                sender.sendForbidden()
                return
            }

            sender.sendResult(database.tables.map { table ->
                RelationalTableEntry().append("name", table.name).append("type", table.type)
            }.toMutableList() as Collection<TableEntry>, mutableListOf("name", "type"), ResultType.LEGACY)
            return
        }

        sender.sendSyntax()
    }

    override fun complete(sender: CommandSender, line: ParsedLine): MutableList<String>? {
        sender.session ?: return null

        val args = line.line().uppercase()

        return when {
            !args.contains(" TABLES") && !args.contains(" DATABASES") -> {
                mutableListOf("tables", "databases")
            }
            else -> {
                null
            }
        }
    }

}
