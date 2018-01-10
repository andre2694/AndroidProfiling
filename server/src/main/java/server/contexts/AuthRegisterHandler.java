package server.contexts;

import com.sun.net.httpserver.HttpExchange;
import dbutils.AuthUserDBManager;
import org.json.JSONObject;
import server.utils.LogManager;

import static server.utils.ServerUtils.SERVER_DEBUG;
import static server.utils.ServerUtils.SERVER_DEPLOY;

public class AuthRegisterHandler extends CustomHandler {

    /**
     * Receives an username, email and password, registering a new user if the given username doesn't match a stored user
     * @param exchange a json post object with the username, email and password
     */
    protected void handleSpecific(HttpExchange exchange) throws Exception {
        JSONObject obj = getJsonPost(exchange);
        if (SERVER_DEBUG) System.out.println(obj.toString());
        if(!obj.has("email") | !obj.has("username") | !obj.has("password")){
            if (SERVER_DEPLOY) LogManager.getInstance().logError("Wrong json keys.");
            String response = "Wrong json keys.";
            responseMessage(exchange, response, 400);
        }
        else {
            String username = (String)obj.get("username");
            String email = (String)obj.get("email");
            String password = (String) obj.get("password");

            if (AuthUserDBManager.hasUser(username)) {
                if (SERVER_DEBUG) System.out.println("Registration failed. User already exists: " + username);
                if (SERVER_DEPLOY) LogManager.getInstance().logError("Registration failed. User already exists: " + username);
                responseMessage(exchange, "Username already exists. Please chose a different username.", 409);
            }
            else {
                if (SERVER_DEBUG) System.out.println("Username: " + username + ". Email: " + email + ". Password: " + password);
                if (SERVER_DEPLOY) LogManager.getInstance().logInfo("Registration succeeded for user: " + username);
                AuthUserDBManager.insertUser(username, password, email);
                responseMessage(exchange, "User created successfully.", 201);
            }
        }
    }
}
