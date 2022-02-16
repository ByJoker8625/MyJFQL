package de.byjoker.myjfql.network.controller

import de.byjoker.myjfql.command.NetworkCommandSender
import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.network.util.*

class QueryController : Controller {

    private val userService = MyJFQL.getInstance().userService

    @Mapping(path = "api/v2/query", method = RequestMethod.POST, login = LoginRequirement.ONLY_SESSION)
    fun query(request: Request) {
        val connection = request.connection
        val session = connection.session ?: return
        val payload = request.payload

        if (payload == null) {
            connection.respond(ErrorResponse("Incomplete request payload!"))
            return
        }

        if (!payload.has("query")) {
            connection.respond(ErrorResponse("Incomplete request payload!"))
            return
        }

        if (!session.validAddress(request.connection.address)) {
            connection.respond(Response(ResponseType.FORBIDDEN))
            return
        }

        val user = userService.getUser(session.userId)

        if (user == null) {
            connection.respond(ErrorResponse("User doesn't exist anymore!"))
            return
        }

        MyJFQL.getInstance().commandService.execute(
            NetworkCommandSender(user, connection),
            payload["query"].asText()
        )
    }

}
