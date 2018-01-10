package server.contexts;

import com.sun.net.httpserver.HttpExchange;
import dbutils.AuthUserDBManager;
import dbutils.UserDBManager;
import models.AuthUser;
import models.User;
import org.json.JSONObject;
import server.utils.LogManager;

import static server.utils.ServerUtils.SERVER_DEBUG;
import static server.utils.ServerUtils.SERVER_DEPLOY;

public class LoginHandler extends CustomHandler {

    /**
     * Receives an username and a password, chedking if the username matches an existing user and if the password matches the stored one
     * @param exchange a json post object with the username and the password
     */
    protected void handleSpecific(HttpExchange exchange) throws Exception {
        JSONObject obj = getJsonPost(exchange);
        if(!obj.has("username") | !obj.has("password")) {
            responseMessage(exchange, "Wrong json keys.", 400);
        }
        else {
            String username = (String)obj.get("username");
            String password = (String) obj.get("password");
            String response;
            int status;

            if (!AuthUserDBManager.hasUser(username)) {
                if (SERVER_DEPLOY) LogManager.getInstance().logError("Login failed. User does not exists: " + username);
                responseMessage(exchange, "User Doest Not Exists", 400);
            }
            else {
                AuthUser u = AuthUserDBManager.getUser(username);
                if (!u.getPassword().equals(password)) {
                    if (SERVER_DEPLOY) LogManager.getInstance().logError("Login failed. User inserted a wrong password: " + username);
                    response = "Wrong password";
                    status = 400;
                }
                else {
                    if (SERVER_DEBUG) System.out.println("Username: " + username + ". Password: " + password);
                    response = "Successful login.";
                    status = 200;
                    if (SERVER_DEPLOY) LogManager.getInstance().logInfo("Login succeeded for user: " + username);
                }
                responseMessage(exchange, response, status);
            }
        }
    }
}
