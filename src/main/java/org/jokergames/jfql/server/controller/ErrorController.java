package org.jokergames.jfql.server.controller;

import io.javalin.http.Context;
import org.jokergames.jfql.core.JFQL;
import org.jokergames.jfql.server.util.Method;

/**
 * @author Janick
 */

public class ErrorController implements Controller {

    @ControllerHandler(path = "$handle.status", status = 404, method = Method.STATUS)
    public void handle404(Context context) {
        context.result(JFQL.getInstance().getServer().getResponseBuilder().buildNotFound().toString()).status(404);
    }

}
