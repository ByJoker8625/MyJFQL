package de.byjoker.myjfql.network.controller

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.RelationalTableEntry
import de.byjoker.myjfql.network.session.DynamicSession
import de.byjoker.myjfql.network.session.Session
import de.byjoker.myjfql.network.session.SessionType
import de.byjoker.myjfql.network.util.*
import de.byjoker.myjfql.user.User
import de.byjoker.myjfql.util.ResultType

class SessionController : Controller {

    private val userService = MyJFQL.getInstance().userService
    private val sessionSession = MyJFQL.getInstance().sessionService

    @Mapping(path = "api/v2/session", method = RequestMethod.POST, login = LoginRequirement.ONLY_NO_SESSION)
    fun openSession(request: Request) {
        val connection = request.connection
        val payload = request.payload

        if (payload == null) {
            connection.respond(ErrorResponse("Undefined payload!"))
            return
        }

        if (!payload.has("user") || !payload.has("password")) {
            connection.respond(ErrorResponse("Incomplete request payload!"))
            return
        }

        val user: User? = userService.getUserByIdentifier(payload["user"].asText())

        if (user == null) {
            connection.respond(Response(ResponseType.FORBIDDEN))
            return
        }

        if (!user.validPassword(payload["password"].asText())) {
            connection.respond(Response(ResponseType.FORBIDDEN))
            return
        }

        val session = DynamicSession(
            addresses = mutableListOf(connection.address),
            databaseId = user.preferredDatabaseId,
            userId = user.id,
        )
        sessionSession.openSession(session)

        connection.respond(
            Result(
                listOf(
                    RelationalTableEntry().append(
                        "token",
                        session.token
                    )
                ), listOf("token"), ResultType.RELATIONAL
            )
        )
    }

    @Mapping(path = "api/v2/session", method = RequestMethod.DELETE, login = LoginRequirement.ONLY_SESSION)
    fun closeSession(request: Request) {
        val connection = request.connection
        val session: Session = connection.session ?: return

        if (session.type != SessionType.DYNAMIC) {
            connection.respond(ErrorResponse("Only dynamic sessions can be closed using this method!"))
            return
        }

        sessionSession.closeSession(session.token)
        connection.respond(Response(ResponseType.SUCCESS))
    }

}
