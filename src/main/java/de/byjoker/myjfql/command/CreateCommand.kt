package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.*
import de.byjoker.myjfql.server.session.Session
import de.byjoker.myjfql.user.User
import org.jline.reader.ParsedLine

@CommandHandler
class CreateCommand :
    Command("create", mutableListOf("COMMAND", "DATABASE", "TABLE", "STRUCTURE", "LIKE", "PRIMARY-KEY")) {

    override fun execute(sender: CommandSender, args: MutableMap<String, MutableList<String>>) {
        val databaseService: DatabaseService = MyJFQL.getInstance().databaseService
        val session: Session? = sender.session

        if (session == null) {
            sender.sendError("Session of this user is invalid!")
            return
        }

        if (args.containsKey("DATABASE")) {
            val database: String? = formatString(args["DATABASE"])

            if (database == null) {
                sender.sendError("Undefined database!")
                return
            }

            if (database.contains("%") || database.contains("#") || database.contains("'")) {
                sender.sendError("Unauthorized characters in the name!")
                return
            }

            if (!sender.allowed(User.ALLOW_CREATE_DATABASES, DatabaseActionPerformType.READ_WRITE)) {
                sender.sendForbidden()
                return
            }

            if (databaseService.existsDatabaseByName(database)) {
                sender.sendError("Database already exists!")
                return
            }

            databaseService.createDatabase(DatabaseImpl(database))
            sender.sendSuccess()
            return
        }

        if (args.containsKey("TABLE")) {
            val database: Database? = session.getDatabase(databaseService)

            if (database == null) {
                sender.sendError("No database is in use for this user!")
                return
            }

            if (!sender.allowed(database.id, DatabaseActionPerformType.READ_WRITE)) {
                sender.sendForbidden()
                return
            }

            val table: String? = formatString(args["TABLE"])

            if (table == null) {
                sender.sendError("Undefined table!")
                return
            }

            if (table.contains("%") || table.contains("#") || table.contains("'")) {
                sender.sendError("Unauthorized characters in the name!")
                return
            }

            if (database.existsTable(table)) {
                sender.sendError("Table already exists!")
                return
            }

            /**
             * In order to be able to keep the syntax of MyJFQL without problems,
             * the default value of the table type is set to 'RELATIONAL'.
             */

            val type: TableType? =
                if (!args.containsKey("LIKE")) TableType.RELATIONAL else TableType.likeTableType(formatString(args["LIKE"]))

            if (type == null) {
                sender.sendError("Unknown table type!")
                return
            }

            when (type) {
                TableType.DOCUMENT -> {
                    val structure: MutableList<String>? =
                        if (!args.containsKey("STRUCTURE")) mutableListOf("_id") else formatList(args["STRUCTURE"])

                    if (structure == null) {
                        sender.sendError("Undefined table structure!")
                        return
                    }

                    database.createTable(DocumentCollection(table, structure))
                }
                else -> {
                    if (!args.containsKey("STRUCTURE")) {
                        sender.sendSyntax()
                        return
                    }

                    val structure: MutableList<String>? = formatList(args["STRUCTURE"])

                    if (structure == null) {
                        sender.sendError("Undefined table structure!")
                        return
                    }

                    val primary: String? =
                        if (args.containsKey("PRIMARY-KEY")) formatString(args["PRIMARY-KEY"]) else structure[0]

                    if (primary == null) {
                        sender.sendError("Undefined primary key!")
                        return
                    }

                    if (!structure.contains(primary)) {
                        sender.sendError("Primary key does not match table structure!")
                        return
                    }

                    database.createTable(
                        RelationalTable(
                            table,
                            structure,
                            primary
                        )
                    )
                }
            }

            sender.sendSuccess()

            databaseService.saveDatabase(database)
            return
        }

        sender.sendSyntax()
    }

    override fun complete(sender: CommandSender, line: ParsedLine): MutableList<String>? {
        sender.session ?: return null

        val args = line.line().uppercase()

        return when {
            !args.contains(" TABLE") && !args.contains(" DATABASE") -> {
                mutableListOf("table", "database")
            }
            else -> {
                null
            }
        }
    }

}
