package server;

import static server.contexts.Contexts.auth;

public class AuthServer extends BaseServer {
    public static int port = 8001;

    public static void main(String[] args) throws Exception {
        initServer(port, "testkey.jks", "password", auth);
    }

}
