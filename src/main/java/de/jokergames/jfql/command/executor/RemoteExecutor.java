package de.jokergames.jfql.command.executor;

import com.sun.net.httpserver.HttpExchange;
import de.jokergames.jfql.exception.NetworkException;
import org.json.JSONObject;

import java.io.OutputStream;

/**
 * @author Janick
 */

public class RemoteExecutor extends Executor {

    private final HttpExchange exchange;

    public RemoteExecutor(String name, HttpExchange exchange) {
        super(name);
        this.exchange = exchange;
    }

    public void sendInfo(JSONObject response) {
        try {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials-Header", "*");

            exchange.sendResponseHeaders(200, response.toString().length());

            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.toString().getBytes());
            outputStream.close();
        } catch (Exception ex) {
            throw new NetworkException(ex);
        }
    }

    public void send(JSONObject response) {
        try {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials-Header", "*");

            exchange.sendResponseHeaders(response.getInt("rCode"), response.toString().length());

            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.toString().getBytes());
            outputStream.close();
        } catch (Exception ex) {
            throw new NetworkException(ex);
        }
    }

    public void sendError(int rCode) {
        try {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials-Header", "*");
            exchange.sendResponseHeaders(rCode, -1);
        } catch (Exception ex) {
            throw new NetworkException(ex);
        }
    }

}
