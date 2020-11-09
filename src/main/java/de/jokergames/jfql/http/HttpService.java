package de.jokergames.jfql.http;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.jokergames.jfql.core.JFQL;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Janick
 */

public class HttpService {

    private final Map<String, HttpHandler> handlers;
    private final HttpServer server;

    public HttpService() throws Exception {
        server = HttpServer.create(new InetSocketAddress(JFQL.getInstance().getConfiguration().getInt("Port")), 0);
        handlers = new HashMap<>();

        server.setExecutor(null);

        server.createContext("/query", new PostHandler());

        for (String path : handlers.keySet()) {
            server.createContext(path, handlers.get(path));
        }

        server.start();
    }

    public void shutdown() {
        server.stop(1);
    }

    public void registerHandler(String s, HttpHandler handler) {
        handlers.put(s, handler);
    }

    public void unregisterHandler(String s) {
        handlers.remove(s);
    }

    public Map<String, HttpHandler> getHandlers() {
        return handlers;
    }
}
