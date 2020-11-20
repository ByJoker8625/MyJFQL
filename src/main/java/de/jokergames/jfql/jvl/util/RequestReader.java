package de.jokergames.jfql.jvl.util;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Janick
 */

public class RequestReader {

    private final HttpServletRequest request;

    public RequestReader(HttpServletRequest request) {
        this.request = request;
    }

    public String stringRequest() throws Exception {
        Reader reader = request.getReader();
        StringBuilder builder = new StringBuilder();

        int read;

        while ((read = reader.read()) != -1) {
            builder.append((char) read);
        }

        return builder.toString();
    }

    public Map<String, String> mapRequest() throws Exception {
        final Map<String, String> map = new HashMap<>();

        for (String string : stringRequest().split("&")) {
            final String[] strings = string.split("=");

            if (strings[1] != null)
                map.put(strings[0], strings[1]);
        }

        return map;
    }

    public JSONObject jsonRequest() throws Exception {
        return new JSONObject(stringRequest());
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}
