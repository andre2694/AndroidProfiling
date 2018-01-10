package server;

import models.Profile;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import server.utils.ServerUtils;
import utils.ServerTesting;
import utils.TestAuthServer;
import utils.TestServer;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;

public class PostHandlerTesting extends ServerTesting {

    protected static Profile profile2;
    protected static Profile profile3;
    protected static String URL;
    protected static String TestUser = "user";
    protected static String UserEmail = "user@tecnico.pt";
    protected static String UserPassword = "password123";

    @BeforeClass
    public static void startServer(){
        ServerUtils.SERVER_DEBUG = false;
        ServerUtils.SERVER_DEPLOY = false;
        try {
            TestServer.main(null);
            TestAuthServer.main(null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void destroyServer() {
        ServerUtils.SERVER_DEBUG = true;
        ServerUtils.SERVER_DEPLOY = true;
        TestServer.server.stop(0);
        TestAuthServer.server.stop(0);
    }

    /**
     * Reads an http connection returning the response string
     * @param l the http connection
     * @return the string with the http response
     */
    protected static String getResponse(HttpURLConnection l){
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(l.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException i){
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(l.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }catch (IOException e) {
                Assert.fail(e.getMessage());
                return null;
            }
        }
    }

    /**
     * Initialize lists and corresponding profiles that use those lists
     */
    protected static void initLists() {
        start();
        ArrayList<String> networks = new ArrayList<String>();
        String network2 = "network2";
        networks.add(network2);
        ArrayList<String> google = new ArrayList<String>();
        String google2 = "google2";
        google.add(google2);
        ArrayList<String> memorizedAcc = new ArrayList<String>();
        String account2 = "account2";
        memorizedAcc.add(account2);
        ArrayList<String> inputs = new ArrayList<String>();
        String input2 = "input2";
        inputs.add(input2);
        ArrayList<String> installedApps = new ArrayList<String>();
        String applications2 = "applications2";
        installedApps.add(applications2);
        profile2 = new Profile(hmac(aesKey, imei+"1"), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress),
                hmac(aesKey, ipAddress), networks, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), google, memorizedAcc, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputs, installedApps);
        profile3 = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion+"1"), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress),
                hmac(aesKey, ipAddress), networks, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), google, memorizedAcc,
                hmac(aesKey, deviceName), hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputs, installedApps);
    }

    /**
     * Creates and sends a POST request to the server giving an http connection and the request string
     * @param con the http connection
     * @param request the request string
     */
    protected void createPostRequest(HttpURLConnection con, String request) throws IOException {
        con.setRequestMethod("POST");
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(request);
        wr.flush();
        wr.close();
    }

    /**
     * Parses the message of the response of a given http connection
     * @param l the http connection
     * @return the message string of the response
     */
    protected String getPostResponseMessage(HttpURLConnection l) {
        String response = getResponse(l);
        JSONObject responseJson = new JSONObject(response);
        return (String) responseJson.get("message");
    }

    protected static PublicKey generatePublicKey() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair key = gen.generateKeyPair();
        return key.getPublic();
    }

    protected static SecretKey generateAesKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    protected void nullKeys(String json) throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, json);
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong json keys.", response);
    }
}
