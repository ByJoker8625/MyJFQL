package org.jokergames.myjfql.server.controller;

import io.javalin.http.Context;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.server.util.Method;

/**
 * @author Janick
 */

public class ErrorController implements Controller {

    @ControllerHandler(path = "$handle.status", status = 404, method = Method.STATUS)
    public void handle404(Context context) {
        context.result(MyJFQL.getInstance().getServer().getResponseBuilder().buildNotFound().toString()).status(404);
    }

}
