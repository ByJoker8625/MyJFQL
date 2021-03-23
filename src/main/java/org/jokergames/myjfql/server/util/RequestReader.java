package org.jokergames.myjfql.server.util;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
        while ((read = reader.read()) != -1) builder.append((char) read);

        return builder.toString();
    }

    public Map<String, String> mapRequest() throws Exception {
        return Arrays.stream(stringRequest().split("&")).map(string -> string.split("=")).filter(strings -> strings[1] != null).collect(Collectors.toMap(strings -> strings[0], strings -> strings[1], (a, b) -> b));
    }

    public JSONObject jsonRequest() throws Exception {
        return new JSONObject(stringRequest());
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}
