package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.*
import de.byjoker.myjfql.lang.Interpreter
import de.byjoker.myjfql.user.Permission

class PushCommand : Command(
    "push", listOf(
        "command",
        "fields",
        "contents",
        "into",
        "where",
        "limit",
        "fully",
        "table",
        "database",
        "user",
        "primary-key",
        "partition-key",
        "structure",
        "typeof",
        "owner"
    )
) {

    override fun execute(sender: CommandSender, args: Map<String, List<String>>) {
        val databaseService = MyJFQL.getInstance().databaseService
        val userService = MyJFQL.getInstance().userService

        if (args.containsKey("database")) {
            if (!sender.permitted(Permission.CREATE_DATABASE)) {
                sender.forbidden()
                return
            }

            val name = formatString(args["database"])

            if (name == null) {
                sender.error("Undefined name!")
                return
            }

            if (name.matches(Interpreter.NAMING_CONVENTIONS_REGEX)) {
                sender.error("Database name doesn't match with naming conventions!")
                return
            }

            val type = DatabaseType.getDatabaseTypeByIdentifier(formatString(args["typeof"]) ?: "DOCUMENT")
                ?: DatabaseType.DOCUMENT

            if (type == DatabaseType.INTERNAL) {
                sender.error("Internal databases can't be created manually!")
                return
            }

            val database = SimpleDatabase(name = name, type = type)

            if (args.containsKey("owner")) {
                val owner = formatString(args["owner"])

                if (owner == null) {
                    sender.error("Undefined owner!")
                    return
                }

                val user = userService.getUserByIdentifier(owner)

                if (user == null) {
                    sender.error("User doesn't exist!")
                    return
                }

                user.grantAccess(DatabasePermissionLevel.READ_WRITE, database.getId())
                userService.saveUser(user)
            }

            databaseService.createDatabase(database)
            sender.success()
            return
        }

        val session = sender.session

        if (session == null) {
            sender.error("Session of this user is invalid!")
            return
        }

        val database = session.getDatabase(databaseService)

        if (database == null) {
            sender.error("No database is in use for this user!")
            return
        }

        if (!sender.permitted(DatabasePermissionLevel.READ_WRITE, database.getId())) {
            sender.forbidden()
            return
        }

        if (args.containsKey("table")) {
            val name = formatString(args["table"])

            if (name == null) {
                sender.error("Undefined name!")
                return
            }

            if (name.matches(Interpreter.NAMING_CONVENTIONS_REGEX)) {
                sender.error("Database name doesn't match with naming conventions!")
                return
            }

            val type =
                TableType.getTableTypeByIdentifier(formatString(args["typeof"]) ?: "RELATIONAL")
                    ?: TableType.DOCUMENT

            val partitioner: String? = formatString(args["partition-key"])

            when (type) {
                TableType.DOCUMENT -> {
                    database.createTable(
                        DocumentCollection(
                            name = name, partitioner = partitioner ?: "_id", database = database
                        )
                    )
                    databaseService.saveDatabase(database)

                    sender.success()
                    return
                }
                TableType.RELATIONAL -> {
                    val structure = formatList(args["structure"])

                    if (structure == null) {
                        sender.error("Undefined structure!")
                        return
                    }

                    if (structure.isEmpty()) {
                        sender.error("Empty structure!")
                        return
                    }

                    val primary = formatString(args["primary-key"]) ?: structure[0]

                    database.createTable(
                        RelationalTable(
                            name = name,
                            structure = structure.toMutableList(),
                            primary = primary,
                            partitioner = partitioner ?: primary,
                            database = database
                        )
                    )
                    databaseService.saveDatabase(database)
                    sender.success()
                    return
                }
            }
        }

        if (!args.containsKey("into")) {
            sender.syntax()
        }

        val into = formatString(args["into"])

        if (into == null) {
            sender.error("Undefined table!")
            return
        }

        val table = database.getTableByIdentifier(into)

        if (table == null) {
            sender.error("Table doesn't exist!")
            return
        }

        when {
            args.containsKey("fields") -> {
                sender.result(MyJFQL.getInstance().interpreter.interpretPushFieldDefinitions(formatString(args["fields"])!!))
                return
            }
            args.containsKey("contents") -> {
                return
            }
            else -> sender.syntax()
        }
    }

}
