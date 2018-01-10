package server.contexts;

import com.sun.net.httpserver.HttpExchange;
import dbutils.UserDBManager;
import models.User;
import org.json.JSONObject;
import server.utils.LogManager;

import static server.utils.ServerUtils.SERVER_DEBUG;
import static server.utils.ServerUtils.SERVER_DEPLOY;

public class ConfirmCodeHandler extends CustomHandler {

    /**
     * Receives an username and a code, checking if the username matches an existing user and if the code matches the expected one
     * @param exchange a json post object with the username and the code
     */
    protected void handleSpecific(HttpExchange exchange) throws Exception {
        JSONObject obj = getJsonPost(exchange);
        if(!obj.has("username") | !obj.has("code")) {
            responseMessage(exchange, "Wrong json keys.", 400);
        }
        else {
            String username = (String)obj.get("username");
            String code = (String) obj.get("code");
            String response;
            int status;

            if (!UserDBManager.hasUser(username)) {
                if (SERVER_DEPLOY) LogManager.getInstance().logError("Code verification failed. User does not exists: " + username);
                responseMessage(exchange, "User Doest Not Exists.", 400);
            }
            else {
                User u = UserDBManager.getUser(username);
                if (!u.getCode().equals(code)) {
                    if (SERVER_DEPLOY) LogManager.getInstance().logError("Code verification failed. User inserted a wrong code: " + username);
                    response = "Wrong code.";
                    status = 400;
                }
                else {
                    if (SERVER_DEBUG) System.out.println("Username: " + username + ". Code: " + code);
                    String message = "Code successfully verified.";
                    String nonce = updateNonce(username);
                    status = 200;
                    JSONObject jo = new JSONObject();
                    jo.put("message", message);
                    jo.put("nonce", nonce);
                    response = jo.toString();
                    if (SERVER_DEPLOY) LogManager.getInstance().logInfo("Code verification succeeded for user: " + username);
                }
                responseMessage(exchange, response, status);
            }
        }
    }

}
