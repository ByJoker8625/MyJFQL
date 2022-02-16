package de.byjoker.myjfql.network

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.network.controller.*
import de.byjoker.myjfql.network.session.Session
import de.byjoker.myjfql.network.util.*
import de.byjoker.myjfql.util.Json
import io.javalin.Javalin
import io.javalin.http.Handler

class HttpNetworkService : NetworkService {

    private val sessionSession = MyJFQL.getInstance().sessionService

    override val controllers: MutableList<Controller> = mutableListOf()
    private val app = Javalin.create()

    override fun registerController(controller: Controller) {
        controllers.add(controller)
    }

    override fun unregisterController(controller: Controller) {
        controllers.remove(controller)
    }

    override fun start(port: Int) {
        app._conf.showJavalinBanner = false
        app.start(port)

        registerController(SessionController())
        registerController(QueryController())
        registerController(LegacyController())

        app.error(404) { context ->
            /**
             * Only responding in PLAIN_TEXT instead of APPLICATION_JSON to support older connectors.
             */

            context.result(Json.stringify(ErrorResponse("Unknown syntax!")))
        }

        controllers.forEach { controller ->
            controller.javaClass.declaredMethods.filter { method ->
                method.isAnnotationPresent(Mapping::class.java)
            }.forEach { method ->
                val mapping = method.getAnnotation(Mapping::class.java)
                val handler = Handler { context ->
                    val session: Session? =
                        if (context.header("Authorization") == null) null else sessionSession.getSession(
                            context.header("Authorization")!!
                        )

                    val connection = HttpNetworkConnection(context, session)
                    val request = NetworkRequest(
                        connection,
                        Json.parse(context.body()),
                        RequestMethod.valueOf(context.method().uppercase())
                    )

                    try {
                        when (mapping.login) {
                            LoginRequirement.ONLY_SESSION -> {
                                if (session == null) {
                                    connection.respond(Response(ResponseType.FORBIDDEN))
                                    return@Handler
                                }

                                method.invoke(controller, request)
                            }
                            LoginRequirement.ONLY_NO_SESSION -> {
                                if (session != null) {
                                    connection.respond(ErrorResponse("You are already logged in!"))
                                    return@Handler
                                }

                                method.invoke(controller, request)
                            }
                            LoginRequirement.SESSION_AND_NO_SESSION -> {
                                method.invoke(controller, request)
                            }
                        }
                    } catch (ex: Exception) {
                        connection.respond(ErrorResponse(ex.message))
                        ex.printStackTrace()
                    }
                }

                when (mapping.method) {
                    RequestMethod.GET -> app.get(mapping.path, handler)
                    RequestMethod.POST -> app.post(mapping.path, handler)
                    RequestMethod.DELETE -> app.delete(mapping.path, handler)
                    RequestMethod.PUT -> app.put(mapping.path, handler)
                }
            }
        }
    }

    override fun shutdown() {
        app.stop()
    }

}
