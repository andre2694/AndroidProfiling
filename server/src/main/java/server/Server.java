package server;

import static server.contexts.Contexts.*;

public class Server extends BaseServer {
    public static int port = 8000;

    public static void main(String[] args) throws Exception {
        initServer(port, "testkey.jks", "password", contexts);
    }
}
