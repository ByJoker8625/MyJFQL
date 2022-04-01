package de.byjoker.myjfql.network.util

import de.byjoker.myjfql.core.MyJFQL.Companion.getInstance
import de.byjoker.myjfql.exception.NetworkException
import de.byjoker.myjfql.network.session.Session
import de.byjoker.myjfql.util.Json.stringify
import io.javalin.http.ContentType
import io.javalin.http.Context
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

class HttpNetworkConnection(private val context: Context, override val session: Session?) : Connection {

    override val address: String
        get() = context.ip()

    override fun respond(response: Response) {
        /**
         * Only responding in PLAIN_TEXT instead of APPLICATION_JSON to support older connectors.
         */

        context.contentType(ContentType.APPLICATION_JSON)
            .header(
                "Access-Control-Allow-Origin",
                getInstance().config.server.trusted.stream().map { trusted -> "$trusted " }
                    .collect(Collectors.joining()))
            .result(stringify(response).toByteArray(StandardCharsets.UTF_8))
    }

    override fun close() {
        throw NetworkException("Can't close http connection!")
    }

}
