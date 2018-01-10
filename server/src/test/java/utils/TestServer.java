package utils;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

import static server.contexts.Contexts.HANDLER;
import static server.contexts.Contexts.URL;
import static server.contexts.Contexts.contexts;

public class TestServer {
    public static HttpServer server;

    public static void main(String[] args) throws Exception {
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        for (Object[] s : contexts) {
            server.createContext((String) s[URL], (HttpHandler) s[HANDLER]);
        }
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started...\n");
    }
}