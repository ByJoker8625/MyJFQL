package org.jokergames.jfql.server.util;

import org.json.JSONObject;

import java.util.List;

/**
 * @author Janick
 */

public class ResponseBuilder {

    public JSONObject buildAnswer(Object answer, List<String> strings) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.REST);
        jsonObject.put("answer", answer);
        jsonObject.put("rCode", ResponseType.REST.getRCode());
        jsonObject.put("structure", strings);

        return jsonObject;
    }

    public JSONObject buildAnswer(Object answer, String[] strings) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.REST);
        jsonObject.put("answer", answer);
        jsonObject.put("rCode", ResponseType.REST.getRCode());
        jsonObject.put("structure", strings);

        return jsonObject;
    }

    public JSONObject buildSyntax() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.SYNTAX_ERROR);
        jsonObject.put("rCode", ResponseType.SYNTAX_ERROR.getRCode());

        return jsonObject;
    }

    public JSONObject buildSuccess() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.SUCCESS);
        jsonObject.put("rCode", ResponseType.SUCCESS.getRCode());

        return jsonObject;
    }

    public JSONObject buildNotFound() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.NOT_FOUND);
        jsonObject.put("rCode", ResponseType.NOT_FOUND.getRCode());

        return jsonObject;
    }

    public JSONObject buildBadMethod(Exception exception) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.BAD_METHOD);
        jsonObject.put("exception", exception);
        jsonObject.put("rCode", ResponseType.BAD_METHOD.getRCode());

        return jsonObject;
    }

    public JSONObject buildForbidden() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.FORBIDDEN);
        jsonObject.put("rCode", ResponseType.FORBIDDEN.getRCode());

        return jsonObject;
    }

}
