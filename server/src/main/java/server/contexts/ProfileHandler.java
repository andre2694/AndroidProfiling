package server.contexts;

import com.sun.net.httpserver.HttpExchange;
import dbutils.ProfileDBManager;
import dbutils.UserDBManager;
import exceptions.InvalidProfileJSONException;
import exceptions.ProfileDoesNotExistsException;
import models.Profile;
import org.json.JSONObject;
import server.utils.LogManager;
import server.utils.ServerUtils;

import static server.utils.ServerUtils.SERVER_DEBUG;
import static server.utils.ServerUtils.SERVER_DEPLOY;

public class ProfileHandler extends CustomHandler {

    /**
     * Receives an username and a profile, checking if a corresponding stored profile exists and whether the profile received matches the stored profile
     * @param exchange a json post object with the username and the profile
     */
    protected void handleSpecific(HttpExchange exchange) throws Exception {
        JSONObject obj = getJsonPost(exchange);
        if (!obj.has("username") | !obj.has("profile")) {
            String response = "Wrong json keys.";
            responseMessage(exchange, response, 400);
        }
        else {
            String username = (String) obj.get("username");
            if (!UserDBManager.hasUser(username)) {
                if (SERVER_DEPLOY) LogManager.getInstance().logError("Profile verification failed. User does not exists: " + username);
                System.out.println("Profile verification failed. User does not exists: " + username);
                responseMessage(exchange, "User does not exists.", 409);
            }
            else {
                String profile = (String) obj.get("profile");
                try {
                    if (SERVER_DEBUG) System.out.println("Profile:" + profile);
                    Profile p = ServerUtils.parseProfile(profile);
                    if (ProfileDBManager.hasProfile(p.getImei())) {
                        try {
                            Profile p1 = ProfileDBManager.getProfile(p.getImei(), username);
                            if (p1.verifyProfile(p)) {
                                p1.updateProfile(p);
                                String newnonce = updateNonce(username);
                                JSONObject jo = new JSONObject();
                                jo.put("nonce", newnonce);
                                jo.put("message", "Correct profile.");
                                jo.put("info", p1.changedInformation);
                                if (SERVER_DEPLOY) LogManager.getInstance().logInfo("Profile verification succeeded for user: " + username + ". Profile: " + p.toString());
                                System.out.println("Profile verification succeeded for user: " + username);
                                responseMessage(exchange, jo.toString(), 201);
                            }
                            else {
                                if (SERVER_DEPLOY) LogManager.getInstance().logError("Profile verification failed. Incorrect profile for user: " + username + ". Profile: " + p.toString() + ". Expected: " + p1.toString());
                                System.out.println("Profile verification failed. Incorrect profile for user: " + username + ". Profile: " + p.toString() + ". Expected: " + p1.toString());
                                String code = UserDBManager.updateCode(username);
                                JSONObject jo = new JSONObject();
                                jo.put("code", code);
                                jo.put("message", "Incorrect profile.");
                                responseMessage(exchange, jo.toString(), 409);
                            }
                        } catch (ProfileDoesNotExistsException e) {
                            if (SERVER_DEPLOY) LogManager.getInstance().logError("Profile verification failed. User " + username + " does not have a profile with the corresponding imei.");
                            System.out.println("Profile verification failed. User " + username + " does not have a profile with the corresponding imei.");
                            String code = UserDBManager.updateCode(username);
                            JSONObject jo = new JSONObject();
                            jo.put("code", code);
                            jo.put("message", "Incorrect profile.");
                            responseMessage(exchange, jo.toString(), 409);
                        }
                    }
                    else {
                        if (SERVER_DEPLOY) LogManager.getInstance().logError("Profile verification failed. Invalid profile. User: " + username);
                        System.out.println("Profile verification failed. Invalid profile. User: " + username);
                        String code = UserDBManager.updateCode(username);
                        JSONObject jo = new JSONObject();
                        jo.put("code", code);
                        jo.put("message", "Profile does not exist.");
                        responseMessage(exchange, jo.toString(), 409);
                    }
                } catch (InvalidProfileJSONException e) {
                    if (SERVER_DEBUG) e.printStackTrace();
                    if (SERVER_DEPLOY) LogManager.getInstance().logError("Wrong profiles keys. User: " + username);
                    System.out.println("Wrong profiles keys. User: " + username);
                    String response = "Wrong profile keys.";
                    responseMessage(exchange, response, 400);
                }
            }
        }
    }
}
