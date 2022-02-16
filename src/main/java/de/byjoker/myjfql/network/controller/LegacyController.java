package de.byjoker.myjfql.network.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.byjoker.myjfql.command.LegacyNetworkCommandSender;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.network.session.DynamicSession;
import de.byjoker.myjfql.network.session.Session;
import de.byjoker.myjfql.network.session.SessionService;
import de.byjoker.myjfql.network.session.SessionType;
import de.byjoker.myjfql.network.util.*;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.UserService;
import de.byjoker.myjfql.util.IDGenerator;

import java.util.Collections;

public class LegacyController implements Controller {

    private final SessionService sessionService = MyJFQL.getInstance().getSessionService();
    private final UserService userService = MyJFQL.getInstance().getUserService();

    @Mapping(path = "api/v1/session/open", method = RequestMethod.POST)
    public void openSession(Request request) {
        final Connection connection = request.getConnection();
        final JsonNode payload = request.getPayload();

        if (payload == null) {
            connection.respond(new ErrorResponse("Incomplete request payload!"));
            return;
        }

        if (!payload.has("user") || !payload.has("password")) {
            boolean canceled = true;

            if (payload.has("auth")) {
                final ObjectNode auth = (ObjectNode) payload.get("auth");

                if (auth.has("user") && auth.has("password")) {
                    canceled = false;

                    auth.put("user", auth.get("user").asText());
                    auth.put("password", auth.get("password").asText());
                }
            }

            if (canceled) {
                connection.respond(new ErrorResponse("Incomplete request payload!"));
                return;
            }
        }

        if (payload.get("user").asText().equals("%TOKEN%")) {
            final Session session = sessionService.getSession(payload.get("password").asText());

            if (session == null) {
                connection.respond(new Response(ResponseType.FORBIDDEN));
                return;
            }

            if (session.getType() != SessionType.STATIC) {
                connection.respond(new ErrorResponse("Only static sessions are join able!"));
                return;
            }

            if (!session.validAddress(connection.getAddress())) {
                connection.respond(new Response(ResponseType.FORBIDDEN));
                return;
            }

            connection.respond(new LegacySessionResult(session.getToken()));
            return;
        }

        final User user = userService.getUserByIdentifier(payload.get("user").asText());

        if (user == null) {
            connection.respond(new Response(ResponseType.FORBIDDEN));
            return;
        }

        if (!user.validPassword(payload.get("password").asText())) {
            connection.respond(new Response(ResponseType.FORBIDDEN));
            return;
        }

        final Session session = new DynamicSession(IDGenerator.generateMixed(25), user.getId(), user.getPreferredDatabaseId(), Collections.singletonList(connection.getAddress()));
        sessionService.openSession(session);

        connection.respond(new LegacySessionResult(session.getToken()));
    }

    @Mapping(path = "api/v1/session/close", method = RequestMethod.POST)
    public void closeSession(Request request) {
        final Connection connection = request.getConnection();
        final JsonNode payload = request.getPayload();

        if (payload == null) {
            connection.respond(new ErrorResponse("Incomplete request payload!"));
            return;
        }

        if (!payload.has("token")) {
            connection.respond(new ErrorResponse("Incomplete request payload!"));
            return;
        }

        final Session session = sessionService.getSession(payload.get("token").asText());

        if (session == null) {
            connection.respond(new Response(ResponseType.FORBIDDEN));
            return;
        }

        if (session.getType() != SessionType.DYNAMIC) {
            connection.respond(new ErrorResponse("Only dynamic sessions can be closed by the client!"));
            return;
        }

        sessionService.closeSession(session.getToken());
        connection.respond(new Response(ResponseType.SUCCESS));
    }

    @Mapping(path = "api/v1/query", method = RequestMethod.POST)
    public void query(Request request) {
        final Connection connection = request.getConnection();
        final JsonNode payload = request.getPayload();

        if (payload == null) {
            connection.respond(new ErrorResponse("Incomplete request payload!"));
            return;
        }

        if (!payload.has("query") || !payload.has("token")) {
            connection.respond(new ErrorResponse("Incomplete request payload!"));
            return;
        }

        final Session session = sessionService.getSession(payload.get("token").asText());

        if (session == null) {
            connection.respond(new Response(ResponseType.FORBIDDEN));
            return;
        }

        if (!session.validAddress(connection.getAddress())) {
            connection.respond(new Response(ResponseType.FORBIDDEN));
            return;
        }

        final User user = userService.getUser(session.getUserId());

        if (user == null) {
            connection.respond(new ErrorResponse("User doesn't exist anymore!"));
            return;
        }

        MyJFQL.getInstance().getCommandService().execute(
                new LegacyNetworkCommandSender(user, connection, session),
                payload.get("query").asText()
        );
    }

}
