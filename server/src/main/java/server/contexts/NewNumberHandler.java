package server.contexts;

import com.sun.net.httpserver.HttpExchange;
import dbutils.ProfileDBManager;
import dbutils.UserDBManager;
import exceptions.ProfileDoesNotExistsException;
import org.json.JSONObject;
import server.utils.LogManager;

import static server.utils.ServerUtils.SERVER_DEBUG;
import static server.utils.ServerUtils.SERVER_DEPLOY;

public class NewNumberHandler extends CustomHandler {

    /**
     * Receives an imei and sim card info, sent by an user who purchased a new sim card
     * @param exchange a json post object with the imei and sim card information
     */
    protected void handleSpecific(HttpExchange exchange) throws Exception {
        JSONObject obj = getJsonPost(exchange);
        if (!obj.has("username") | !obj.has("nonce") | !obj.has("imei") | !obj.has("sim_operator") | !obj.has("imsi_number") | !obj.has("sim_operator_name") | !obj.has("sim_country_iso") | !obj.has("sim_serial_number")) {
            responseMessage(exchange, "Wrong json keys.", 400);
        }
        else {
            String username = obj.getString("username");
            if (!UserDBManager.hasUser(username)) {
                if (SERVER_DEPLOY) LogManager.getInstance().logInfo("Username does not exists: " + username);
                responseMessage(exchange, "Wrong username.", 409);
            }
            else {
                String nonce = obj.getString("nonce");
                if (!checkNonce(username, nonce)) {
                    if (SERVER_DEPLOY) LogManager.getInstance().logError("Nonce verification failed. User: " + username);
                    responseMessage(exchange, "Nonce verification failed.", 409);
                }
                else {
                    try {
                        String imei = obj.getString("imei");
                        String operator = obj.getString("sim_operator");
                        String serial = obj.getString("sim_serial_number");
                        String country = obj.getString("sim_country_iso");
                        String name = obj.getString("sim_operator_name");
                        String imsi = obj.getString("imsi_number");
                        if (SERVER_DEPLOY) LogManager.getInstance().logInfo("New sim card information for imei: " + imei + ". User: " + username);
                        ProfileDBManager.updateNumber(imei, operator, serial, country, name, imsi);
                        String code = UserDBManager.updateCode(username);
                        JSONObject jo = new JSONObject();
                        jo.put("code", code);
                        jo.put("message", "Number updated.");
                        responseMessage(exchange, jo.toString(), 200);
                    } catch (ProfileDoesNotExistsException e) {
                        if (SERVER_DEBUG) e.printStackTrace();
                        if (SERVER_DEPLOY) LogManager.getInstance().logError("No matching profile. User: " + username);
                        responseMessage(exchange, "No matching profile.", 409);
                    }
                }
            }
        }
    }
}
