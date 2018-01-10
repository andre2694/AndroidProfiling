package server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dbutils.UserDBManager;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.URLDecoder;
import java.text.ParseException;

import static server.utils.ServerUtils.SERVER_DEBUG;

public abstract class CustomHandler implements HttpHandler {

    /**
     * Handles an http exchange, creating a new thread that will deal with the request
     * @param httpExchange the http exhcange to be handled
     */
    public void handle(HttpExchange httpExchange) throws IOException {
        Thread t = new Thread(new Handler(httpExchange));
        t.start();
    }

    class Handler implements Runnable {
        private HttpExchange httpExchange;

        public Handler(HttpExchange exchange) {
            httpExchange = exchange;
        }

        public void run() {
            try {
                handleSpecific(httpExchange);
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
                try {
                    responseMessage(httpExchange, "Internal Server Error", 500);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    protected abstract void handleSpecific(HttpExchange httpExchange) throws Exception;

    /**
     * Sends a message within the response http exchange object
     * @param exchange the http exchange object of the response
     * @param response the response message
     * @param status the response status code
     */
    protected void responseMessage(HttpExchange exchange, String response, int status) throws IOException {
        if (SERVER_DEBUG) System.out.println(response);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", response);
        jsonObject.put("status_code", status);
        String resp = jsonObject.toString();
        exchange.sendResponseHeaders(status, resp.length());
        OutputStream os = exchange.getResponseBody();
        os.write(resp.getBytes());
        os.close();
    }

    /**
     * Parse an http exchange object, retrieving the json object that is inside that object
     * @param exchange the http exchange object to be parsed
     * @return the json object that is contained in the http exchange object
     */
    protected JSONObject getJsonPost(HttpExchange exchange) throws IOException, ParseException {
        InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);

        String value = br.readLine();
        String query = URLDecoder.decode(value, "UTF-8");
        return new JSONObject(query);
    }

    /**
     * Verify if the receive nonce checks the expected one
     * @param username the username of the user
     * @param nonce the nonce sent by the user
     * @return true if the sent nonce matches the stored one, false otherwise
     */
    protected boolean checkNonce(String username, String nonce) {
        try {
            String stored = UserDBManager.getCurrentNonce(username);
            return stored.equals(nonce);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates the nonce for a specified user
     * @param username the username of the user
     * @return the freshly generated nonce
     */
    protected String updateNonce(String username) {
        try {
/*            SecretKey aesKey = UserDBManager.getSymmetricKey(username);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            String nonce = UserDBManager.updateNonce(username);
            System.out.println("NONCE: " + nonce);
            byte[] encrypted = cipher.doFinal(nonce.getBytes());
            return Base64.encodeBase64String(encrypted); */
            return UserDBManager.updateNonce(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}