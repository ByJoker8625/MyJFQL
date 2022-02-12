package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.database.RelationalTableEntry
import de.byjoker.myjfql.database.SimpleDatabase
import de.byjoker.myjfql.database.TableEntry
import de.byjoker.myjfql.user.SimpleUser
import de.byjoker.myjfql.util.ResultType
import java.util.*

@CommandHandler
class UserCommand : ConsoleCommand(
    "user", mutableListOf(
        "COMMAND",
        "CREATE",
        "PASSWORD",
        "GRANT",
        "REVOKE",
        "ACCESS",
        "DATABASE",
        "AT",
        "FROM",
        "DISPLAY",
        "LIST",
        "DELETE"
    )
) {

    override fun executeAsConsole(sender: ConsoleCommandSender, args: MutableMap<String, MutableList<String>>) {
        val userService = MyJFQL.getInstance().userService
        val databaseService = MyJFQL.getInstance().databaseService
        if (args.containsKey("CREATE") && args.containsKey("PASSWORD")) {
            val name = formatString(args["CREATE"])
            val password = formatString(args["PASSWORD"])
            if (name == null) {
                sender.sendError("Undefined name!")
                return
            }
            if (name.contains("%") || name.contains("#") || name.contains("'")) {
                sender.sendError("Unauthorized characters in the name!")
                return
            }
            if (password == null) {
                sender.sendError("Undefined password!")
                return
            }
            if (password.length < 8) {
                sender.sendError("Password to short!")
                return
            }
            if (userService.existsUserByName(name)) {
                sender.sendError("User already exists!")
                return
            }

            val user = SimpleUser(name = name, password = password)

            if (args.containsKey("DATABASE")) {
                val databaseName: String = if (args["DATABASE"]!!.size == 0) name else formatString(args["DATABASE"])!!
                if (databaseService.existsDatabaseByName(databaseName)) {
                    sender.sendError("Database already exists!")
                    return
                }
                val database = SimpleDatabase(databaseName)
                databaseService.createDatabase(database)
                user.grantAccess(database.id, DatabasePermissionLevel.READ_WRITE)
                user.preferredDatabaseId = database.id
            }

            userService.createUser(user)
            sender.sendSuccess()
            return
        }

        if (args.containsKey("DELETE")) {
            val userIdentifier = formatString(args["DELETE"])
            if (userIdentifier == null) {
                sender.sendError("Undefined user!")
                return
            }
            if (!userService.existsUserByIdentifier(userIdentifier)) {
                sender.sendError("User doesn't exists!")
                return
            }
            val userId = userService.getUserByIdentifier(userIdentifier).id
            MyJFQL.getInstance().sessionService.closeSessions(userId)
            userService.deleteUser(userId)
            sender.sendSuccess()
            return
        }

        if (args.containsKey("GRANT") && args.containsKey("ACCESS") && args.containsKey("AT")) {
            val userIdentifier = formatString(args["GRANT"])
            val access = formatString(args["ACCESS"])
            val databaseIdentifier = formatString(args["AT"])
            if (userIdentifier == null) {
                sender.sendError("Undefined user!")
                return
            }
            if (access == null) {
                sender.sendError("Undefined access action!")
                return
            }
            if (databaseIdentifier == null) {
                sender.sendError("Undefined database!")
                return
            }
            if (!userService.existsUserByIdentifier(userIdentifier)) {
                sender.sendError("User doesn't exists!")
                return
            }
            if (!databaseService.existsDatabaseByIdentifier(databaseIdentifier)) {
                sender.sendError("Database doesn't exists!")
                return
            }
            val action: DatabasePermissionLevel
            action = try {
                DatabasePermissionLevel.valueOf(access.uppercase(Locale.getDefault()))
            } catch (ex: Exception) {
                sender.sendError("Unknown access action!")
                return
            }
            val user = userService.getUserByIdentifier(userIdentifier)
            user.grantAccess(databaseService.getDatabaseByIdentifier(databaseIdentifier).id, action)
            userService.saveUser(user)
            sender.sendSuccess()
            return
        }

        if (args.containsKey("REVOKE") && args.containsKey("ACCESS") && args.containsKey("FROM")) {
            val userIdentifier = formatString(args["REVOKE"])
            val databaseIdentifier = formatString(args["FROM"])
            if (userIdentifier == null) {
                sender.sendError("Undefined user!")
                return
            }
            if (databaseIdentifier == null) {
                sender.sendError("Undefined database!")
                return
            }
            if (!userService.existsUserByIdentifier(userIdentifier)) {
                sender.sendError("User doesn't exists!")
                return
            }
            if (!databaseService.existsDatabaseByIdentifier(databaseIdentifier)) {
                sender.sendError("Database doesn't exists!")
                return
            }
            val user = userService.getUserByName(userIdentifier)
            user.revokeAccess(databaseService.getDatabaseByIdentifier(databaseIdentifier).id)
            userService.saveUser(user)
            sender.sendSuccess()
            return
        }

        if (args.containsKey("DISPLAY")) {
            val userIdentifier = formatString(args["DISPLAY"])

            if (userIdentifier == null) {
                sender.sendError("Undefined user!")
                return
            }

            if (!userService.existsUserByIdentifier(userIdentifier)) {
                sender.sendError("User doesn't exists!")
                return
            }

            val selectedUser = userService.getUserByIdentifier(userIdentifier)
            val tableEntry = RelationalTableEntry()

            tableEntry.insert("id", selectedUser.id)
            tableEntry.insert("name", selectedUser.name)
            tableEntry.insert("accesses", selectedUser.accesses.toString())
            tableEntry.insert("preferred_database_id", java.lang.String.valueOf(selectedUser.preferredDatabaseId))

            sender.sendResult(
                mutableListOf(tableEntry) as Collection<TableEntry>,
                mutableListOf("id", "name", "accesses", "preferred_database_id"),
                ResultType.LEGACY
            )
            return
        }
        if (args.containsKey("LIST")) {
            sender.sendResult(
                userService.users.map { user -> RelationalTableEntry().append("user_name", user.name) }
                    .toMutableList() as Collection<TableEntry>,
                mutableListOf("user_name"),
                ResultType.LEGACY
            )
            return
        }
        sender.sendSyntax()
    }
}
