package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.database.DatabaseType
import de.byjoker.myjfql.database.SimpleDatabase
import de.byjoker.myjfql.lang.Interpreter

class PushCommand : Command(
    "push", listOf(
        "command",
        "fields",
        "contents",
        "content",
        "table",
        "database",
        "user",
        "where",
        "limit",
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

        sender.syntax()
    }

}
