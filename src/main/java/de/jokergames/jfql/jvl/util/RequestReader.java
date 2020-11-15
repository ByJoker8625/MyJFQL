package de.jokergames.jfql.jvl.util;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.Reader;

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

    public JSONObject jsonRequest() throws Exception {
        return new JSONObject(stringRequest());
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}
