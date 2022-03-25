package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.network.session.StaticSession
import de.byjoker.myjfql.util.ResultType

@CommandHandler
class SessionsCommand :
    ConsoleCommand("sessions", listOf("COMMAND", "OF", "OPEN", "CLOSE", "ADAPT", "DATABASE", "ADDRESSES", "AT")) {

    override fun executeAsConsole(sender: ConsoleCommandSender, args: Map<String, List<String>>) {
        val sessionService = MyJFQL.getInstance().sessionService
        val databaseService = MyJFQL.getInstance().databaseService
        val userService = MyJFQL.getInstance().userService

        if (!args.containsKey("OF")) {
            sender.sendSyntax()
            return
        }

        val userIdenf = formatString(args["OF"])

        if (userIdenf == null) {
            sender.sendError("Undefined user!")
            return
        }

        val user = userService.getUserByIdentifier(userIdenf)

        if (user == null) {
            sender.sendError("User doesn't exist!")
            return
        }

        if (args.size == 2) {
            sender.sendResult(sessionService.getSessionsByUserId(user.id).map { session -> session.asTableEntry() }
                .toList(), listOf("token", "type", "user_id", "database_id", "addresses"), ResultType.RELATIONAL)
            return
        }

        if (args.containsKey("OPEN")) {
            val session = StaticSession(
                userId = user.id,
                databaseId = user.preferredDatabaseId,
                addresses = mutableListOf("127.0.0.1")
            )

            if (args.containsKey("DATABASE")) {
                val databaseIdenf = formatString(args["DATABASE"])

                if (databaseIdenf == null) {
                    sender.sendError("Undefined database!")
                    return
                }

                val database = databaseService.getDatabaseByIdentifier(databaseIdenf)

                if (database == null) {
                    sender.sendError("Database doesn't exist!")
                    return
                }

                session.databaseId = database.id
            }

            if (args.containsKey("ADDRESSES")) {
                val addresses = formatList(args["ADDRESSES"])

                if (addresses == null) {
                    sender.sendError("Undefined addresses!")
                    return
                }

                session.addresses = addresses.toMutableList()
            }

            sessionService.openSession(session)

            sender.sendResult(
                listOf(session.asTableEntry()),
                listOf("token", "type", "user_id", "database_id", "addresses"),
                ResultType.RELATIONAL
            )
            return
        }

        if (args.containsKey("CLOSE")) {
            val sessionId = formatString(args["CLOSE"])

            if (sessionId == null) {
                sender.sendError("Undefined session!")
                return
            }

            if (sessionId == "*") {
                sessionService.closeSessions(user.id)
                sender.sendSuccess()
                return
            }

            sessionService.closeSession(sessionId)
            sender.sendSuccess()
            return
        }

        if (args.containsKey("ADAPT") && args.containsKey("AT")) {
            val sessionId = formatString(args["AT"])

            if (sessionId == null) {
                sender.sendError("Undefined session!")
                return
            }

            val session = sessionService.getSession(sessionId)

            if (session == null) {
                sender.sendError("Session doesn't exist!")
                return
            }

            if (session.userId != user.id) {
                sender.sendError("Session doesn't belongs to user!")
                return
            }

            if (args.containsKey("DATABASE")) {
                val databaseIdenf = formatString(args["DATABASE"])

                if (databaseIdenf == null) {
                    sender.sendError("Undefined database!")
                    return
                }

                val database = databaseService.getDatabaseByIdentifier(databaseIdenf)

                if (database == null) {
                    sender.sendError("Database doesn't exist!")
                    return
                }

                session.databaseId = database.id
                sessionService.saveSession(session)

                sender.sendSuccess()
                return
            }

            if (args.containsKey("ADDRESSES")) {
                val addresses = formatList(args["ADDRESSES"])

                if (addresses == null) {
                    sender.sendError("Undefined addresses!")
                    return
                }

                session.addresses = addresses.toMutableList()
                sessionService.saveSession(session)

                sender.sendSuccess()
                return
            }
        }

        sender.sendSyntax()
    }

}
