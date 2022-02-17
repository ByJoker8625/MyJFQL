package de.byjoker.myjfql.network.util;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.exception.NetworkException;
import de.byjoker.myjfql.network.session.Session;
import de.byjoker.myjfql.util.Json;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class HttpNetworkConnection implements Connection {

    private final Context context;
    private final Session session;

    public HttpNetworkConnection(Context context, Session session) {
        this.context = context;
        this.session = session;
    }

    @NotNull
    @Override
    public String getAddress() {
        return context.ip();
    }

    @Nullable
    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void respond(@NotNull Response response) {
        /**
         * Only responding in PLAIN_TEXT instead of APPLICATION_JSON to support older connectors.
         */


        context.contentType(ContentType.TEXT_PLAIN)
                .header("Access-Control-Allow-Origin", MyJFQL.getInstance().getConfig().getServer().getTrusted().stream().map(trusted -> trusted + " ").collect(Collectors.joining()))
                .result(Json.stringify(response)
                        .getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() {
        throw new NetworkException("Can't close http connection!");
    }

    public Context getContext() {
        return context;
    }
}
