package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.DatabaseAction
import de.byjoker.myjfql.database.DatabaseType
import de.byjoker.myjfql.database.TableType

@CommandHandler
class FormatCommand : ConsoleCommand("format", mutableListOf("COMMAND", "DATABASE", "TABLE", "INTO")) {

    override fun executeAsConsole(sender: ConsoleCommandSender, args: MutableMap<String, MutableList<String>>) {
        val databaseService = MyJFQL.getInstance().databaseService

        if (!args.containsKey("INTO")) {
            sender.sendSyntax()
            return
        }

        if (args.containsKey("DATABASE")) {
            val identifier: String? = formatString(args["DATABASE"])

            if (identifier == null) {
                sender.sendError("Undefined database!")
                return
            }

            if (!databaseService.existsDatabaseByIdentifier(identifier)) {
                sender.sendError("Database doesn't exist!")
                return
            }

            val into: String? = formatString(args["INTO"])

            if (into == null) {
                sender.sendError("Undefined type!")
                return
            }

            val type: DatabaseType? = DatabaseType.likeDatabaseType(into)

            if (type == null) {
                sender.sendError("Type doesn't exist!")
                return
            }

            val database = databaseService.getDatabaseByIdentifier(identifier)
            database.reformat(type, databaseService)

            sender.sendSuccess()
            return
        }

        if (args.containsKey("TABLE")) {
            val database = sender.session.getDatabase(databaseService)

            if (database == null) {
                sender.sendError("No database is in use for this user!")
                return
            }

            if (!sender.allowed(database.id, DatabaseAction.READ_WRITE)) {
                sender.sendForbidden()
                return
            }

            val name: String? = formatString(args["TABLE"])

            if (name == null) {
                sender.sendError("Undefined table!")
                return
            }

            if (!database.existsTable(name)) {
                sender.sendError("Table doesn't exist!")
                return
            }

            val into: String? = formatString(args["INTO"])

            if (into == null) {
                sender.sendError("Undefined type!")
                return
            }

            val type: TableType? = TableType.likeTableType(into)

            if (type == null) {
                sender.sendError("Type doesn't exist!")
                return
            }

            val table = database.getTable(name)

            if (table.type == type) {
                sender.sendError("Table is already an instance of this type!")
                return
            }

            if (table.type == TableType.DOCUMENT) {
                sender.sendError("A non-relational table cannot be formatted into a rational table, since it is not possible to relationalize all values!")
                return
            }

            database.deleteTable(table.name)

            try {
                database.createTable(table.reformat(type))
            } catch (ex: Exception) {
                sender.sendError("Failed to format table: ${ex.message}!")
                database.createTable(table)
                return
            }

            sender.sendSuccess()
            return
        }

        sender.sendSyntax()
    }

}
