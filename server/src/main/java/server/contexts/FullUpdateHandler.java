package server.contexts;


import com.sun.net.httpserver.HttpExchange;
import dbutils.ProfileDBManager;
import dbutils.UserDBManager;
import exceptions.InvalidProfileJSONException;
import models.Profile;
import org.json.JSONObject;
import server.utils.LogManager;
import server.utils.ServerUtils;

import static server.utils.ServerUtils.SERVER_DEBUG;
import static server.utils.ServerUtils.SERVER_DEPLOY;

public class FullUpdateHandler extends CustomHandler {

    protected void handleSpecific(HttpExchange exchange) throws Exception {
        JSONObject obj = getJsonPost(exchange);
        if (!obj.has("username") | !obj.has("profile")) {
            responseMessage(exchange, "Wrong json keys.", 400);
        }
        else {
            String username = (String) obj.get("username");

            if (!UserDBManager.hasUser(username)) {
                if (SERVER_DEPLOY) LogManager.getInstance().logError("Full Update failed. User does not exists: " + username);
                responseMessage(exchange, "User does not exists.", 409);
            }
            else {
                String profile = (String) obj.get("profile");
                try {
                    if (SERVER_DEBUG) System.out.println("Username: " + username + ". New Profile:" + profile);
                    Profile p = ServerUtils.parseProfile(profile);
                    // If an old profile exists, we must remove it before inserting a new one
                    if (ProfileDBManager.hasProfile(p.getImei())) {
                        ProfileDBManager.removeProfile(p.getImei());
                        if (SERVER_DEPLOY) LogManager.getInstance().logInfo("Profile successfully removed for user: " + username + ". Profile removed: " + profile);
                    }
                    // If the user logged in from a different phone or the key was lost and the profile is different
                    ProfileDBManager.insertProfile(p, username);
                    if (SERVER_DEPLOY) LogManager.getInstance().logInfo("Profile successfully inserted for user: " + username + ". Profile: " + profile);
                    String nonce = UserDBManager.updateNonce(username);
                    JSONObject jo = new JSONObject();
                    jo.put("nonce", nonce);
                    jo.put("message", "Successfully updated.");
                    responseMessage(exchange, jo.toString(), 201);
                    if (SERVER_DEPLOY) LogManager.getInstance().logInfo("Full update succeeded for user: " + username);
                } catch (InvalidProfileJSONException e) {
                    if (SERVER_DEBUG) e.printStackTrace();
                    if (SERVER_DEPLOY) LogManager.getInstance().logError("Wrong profile keys for user: " + username);
                    String response = "Wrong profile keys.";
                    responseMessage(exchange, response, 400);
                }
            }
        }
    }
}
