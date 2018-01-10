package utils;


import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

import static server.contexts.Contexts.*;

public class TestAuthServer {
    public static HttpServer server;

    public static void main(String[] args) throws Exception {
        server = HttpServer.create(new InetSocketAddress(8001), 0);
        for (Object[] s : auth) {
            server.createContext((String) s[URL], (HttpHandler) s[HANDLER]);
        }
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Auth Server started...\n");
    }
}
