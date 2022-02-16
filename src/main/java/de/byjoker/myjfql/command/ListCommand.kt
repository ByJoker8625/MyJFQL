package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.database.RelationalTableEntry
import de.byjoker.myjfql.util.ResultType
import org.jline.reader.ParsedLine

@CommandHandler
class ListCommand : Command("list", listOf("COMMAND", "TABLES", "DATABASES")) {

    override fun execute(sender: CommandSender, args: Map<String, List<String>>) {
        val databaseService = MyJFQL.getInstance().databaseService

        if (args.containsKey("DATABASES")) {
            sender.sendResult(databaseService.databases.filter { database ->
                sender.allowed(
                    database.id,
                    DatabasePermissionLevel.READ
                )
            }.map { database ->
                RelationalTableEntry().append("name", database.name).append("type", database.type)
            }, mutableListOf("name", "type"), ResultType.RELATIONAL)
            return
        }

        if (args.containsKey("TABLES")) {
            val database = sender.session.getDatabase(databaseService)

            if (database == null) {
                sender.sendError("No database is in use for this user!")
                return
            }

            if (!sender.allowed(database.id, DatabasePermissionLevel.READ_WRITE)) {
                sender.sendForbidden()
                return
            }

            sender.sendResult(database.tables.map { table ->
                RelationalTableEntry().append("name", table.name).append("type", table.type)
            }, listOf("name", "type"), ResultType.RELATIONAL)
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
