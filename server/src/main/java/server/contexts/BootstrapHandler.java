package server.contexts;

import com.sun.net.httpserver.HttpExchange;
import dbutils.ProfileDBManager;
import dbutils.UserDBManager;
import exceptions.InvalidProfileJSONException;
import models.Profile;
import org.json.JSONObject;
import server.utils.LogManager;
import server.utils.ServerUtils;
import sun.misc.BASE64Decoder;
import sun.rmi.runtime.Log;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import static server.utils.ServerUtils.SERVER_DEBUG;
import static server.utils.ServerUtils.SERVER_DEPLOY;

public class BootstrapHandler extends CustomHandler {

    /**
     * Receives an username and a profile and stores the profile in the database, if the profile is valid and the username exists
     * @param exchange a json post object with the username and the profile
     */
    protected void handleSpecific(HttpExchange exchange) throws Exception {
        JSONObject obj = getJsonPost(exchange);
        // If the username doesn't exists or the profile wasn't send in the json object
        if (!obj.has("username") | !obj.has("profile") | !obj.has("public_key") | !obj.has("symmetric_key")) {
            if (SERVER_DEPLOY) LogManager.getInstance().logError("Wrong json keys.");
            String response = "Wrong json keys.";
            responseMessage(exchange, response, 400);
        }
        else {
            String username = (String) obj.get("username");
            if (UserDBManager.hasUser(username)) {
                if (SERVER_DEPLOY) LogManager.getInstance().logError("Bootstrap failed. User already exists: " + username);
                responseMessage(exchange, "User Already Exists.", 409);
            }
            else {
                String pubk = (String) obj.get("public_key");
                byte[] keyBytes = (new BASE64Decoder()).decodeBuffer(pubk);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(keySpec);
                String symmetric = (String) obj.get("symmetric_key");
                byte[] simBytes = (new BASE64Decoder()).decodeBuffer(symmetric);
                SecretKey secretKeySpec = new SecretKeySpec(simBytes, "AES");
                String profile = (String) obj.get("profile");

                if (SERVER_DEBUG) System.out.println("Username: " + username + ".");

                try {
                    if (SERVER_DEBUG) System.out.println("Profile:" + profile);
                    String code = UserDBManager.insertUser(username, publicKey, secretKeySpec);
                    // Parse a profile instance of the received profile
                    Profile p = ServerUtils.parseProfile(profile);
                    // Inserts the profile in the database
                    ProfileDBManager.insertProfile(p, username);
                    JSONObject jo = new JSONObject();
                    jo.put("code", code);
                    jo.put("message", "Bootstrap succeeded.");
                    responseMessage(exchange, jo.toString(), 201);
                    if (SERVER_DEPLOY) LogManager.getInstance().logInfo("Bootstrap succeeded for user: " + username + ". Profile: " + profile);
                } catch (InvalidProfileJSONException e) {
                    if (SERVER_DEBUG) e.printStackTrace();
                    if (SERVER_DEPLOY) LogManager.getInstance().logError("Wrong profile keys for user: " + username + ". Profile: " + profile);
                    responseMessage(exchange, "Wrong profile keys.", 400);
                }
            }
        }
    }
}
