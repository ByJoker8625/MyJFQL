package de.jokergames.jfql.http.util;

import org.json.JSONObject;

import java.util.List;

/**
 * @author Janick
 */

public class Builder {

    public JSONObject buildAnswer(Object answer, List<String> strings) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", Type.REST);
        jsonObject.put("answer", answer);
        jsonObject.put("rCode", Type.REST.rCode);
        jsonObject.put("structure", strings);

        return jsonObject;
    }

    public JSONObject buildAnswer(Object answer, String[] strings) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", Type.REST);
        jsonObject.put("answer", answer);
        jsonObject.put("rCode", Type.REST.rCode);
        jsonObject.put("structure", strings);

        return jsonObject;
    }

    public JSONObject buildSyntax() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", Type.SYNTAX_ERROR);
        jsonObject.put("rCode", Type.SYNTAX_ERROR.rCode);

        return jsonObject;
    }

    public JSONObject buildSuccess() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", Type.SUCCESS);
        jsonObject.put("rCode", Type.SUCCESS.rCode);

        return jsonObject;
    }

    public JSONObject buildNotFound() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", Type.NOT_FOUND);
        jsonObject.put("rCode", Type.NOT_FOUND.rCode);

        return jsonObject;
    }

    public JSONObject buildBadMethod(Exception exception) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", Type.BAD_METHOD);
        jsonObject.put("exception", exception);
        jsonObject.put("rCode", Type.BAD_METHOD.rCode);

        return jsonObject;
    }

    public JSONObject buildForbidden() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", Type.FORBIDDEN);
        jsonObject.put("rCode", Type.FORBIDDEN.rCode);

        return jsonObject;
    }

}
