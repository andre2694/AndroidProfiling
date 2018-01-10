package server.contexts;


import com.sun.net.httpserver.HttpExchange;
import dbutils.AuthUserDBManager;
import dbutils.UserDBManager;
import models.AuthUser;
import models.User;
import org.json.JSONObject;
import server.utils.LogManager;

import static server.utils.ServerUtils.SERVER_DEPLOY;

public class CancelAccountHandler extends CustomHandler {

    /**
     * Receives an username, password and nonce, removing the given user if the username exists and the password and nonce matches the expected ones
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
                if (SERVER_DEPLOY) LogManager.getInstance().logError("Removing account failed. User does not exists: " + username);
                responseMessage(exchange, "User Doest Not Exists", 409);
            }
            else {
                AuthUser u = AuthUserDBManager.getUser(username);
                if (!u.getPassword().equals(password)) {
                    if (SERVER_DEPLOY) LogManager.getInstance().logError("Removing account failed. User inserted a wrong password: " + username);
                    response = "Wrong password";
                    status = 409;
                }
                else {
                    AuthUserDBManager.removeUser(username);
                    response = "User successfully removed.";
                    status = 200;
                    if (SERVER_DEPLOY) LogManager.getInstance().logInfo("Account successfully removed: " + username);
                }
                responseMessage(exchange, response, status);
            }
        }
    }
}
