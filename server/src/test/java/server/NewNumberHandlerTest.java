package server;

import dbutils.ProfileDBManager;
import dbutils.UserDBManager;
import models.Profile;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;

public class NewNumberHandlerTest extends PostHandlerTesting {

    @BeforeClass
    public static void setup() throws Exception {
        PublicKey publicKey = generatePublicKey();
        start();
        UserDBManager.insertUser(TestUser, publicKey, aesKey);
        URL = "http://localhost:8000/newnumber";
        initLists();
        ProfileDBManager.insertProfile(profile, TestUser);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        UserDBManager.removeAllUsers();
        ProfileDBManager.removeAllProfiles();
    }

    private String createJson(String username, String nonce, String imei, String simoperator, String simoperatorname, String simserialnumber, String simcountryiso, String imsinumber) {
        JSONObject jo = new JSONObject();
        jo.put("username", username);
        jo.put("nonce", nonce);
        if (imei != null)
            jo.put("imei", hmac(aesKey, imei));
        if (simoperator != null)
            jo.put("sim_operator", hmac(aesKey, simoperator));
        if (simoperatorname != null)
            jo.put("sim_operator_name", hmac(aesKey, simoperatorname));
        if (simserialnumber != null)
            jo.put("sim_serial_number", hmac(aesKey, simserialnumber));
        if (simcountryiso != null)
            jo.put("sim_country_iso", hmac(aesKey, simcountryiso));
        if (imsinumber != null)
            jo.put("imsi_number", hmac(aesKey, imsinumber));
        try {
            return URLEncoder.encode(jo.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Test
    public void successUpdate() throws Exception {
        // Check profile
        Profile p = ProfileDBManager.getProfile(hmac(aesKey, imei), TestUser);
        Assert.assertEquals(hmac(aesKey, imsiNumber), p.getImsiNumber());
        Assert.assertEquals(hmac(aesKey, simOperator), p.getSimOperator());
        Assert.assertEquals(hmac(aesKey, simOperatorName), p.getSimOperatorName());
        Assert.assertEquals(hmac(aesKey, simSerialNumber), p.getSimSerialNumber());
        Assert.assertEquals(hmac(aesKey, simCountryIso), p.getSimCountryIso());
        // Check updated info
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, UserDBManager.getCurrentNonce(TestUser), imei, simOperator + "1", simOperatorName + "1", simSerialNumber + "1", simCountryIso + "1", imsiNumber + "1"));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 200);
        String response = getPostResponseMessage(l);
        JSONObject obj = new JSONObject(response);
        p = ProfileDBManager.getProfile(hmac(aesKey, imei), TestUser);
        Assert.assertEquals("Number updated.", obj.getString("message"));
        Assert.assertNotNull(obj.getString("code"));
        Assert.assertEquals(hmac(aesKey, imsiNumber + "1"), p.getImsiNumber());
        Assert.assertEquals(hmac(aesKey, simOperator + "1"), p.getSimOperator());
        Assert.assertEquals(hmac(aesKey, simOperatorName + "1"), p.getSimOperatorName());
        Assert.assertEquals(hmac(aesKey, simSerialNumber + "1"), p.getSimSerialNumber());
        Assert.assertEquals(hmac(aesKey, simCountryIso + "1"), p.getSimCountryIso());
    }

    @Test
    public void wrongUsername() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson("blah", UserDBManager.getCurrentNonce(TestUser),imei, simOperator, simOperatorName, simSerialNumber, simCountryIso, imsiNumber));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 409);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong username.", response);
    }

    @Test
    public void wrongNonce() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, "nonce",imei, simOperator, simOperatorName, simSerialNumber, simCountryIso, imsiNumber));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 409);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Nonce verification failed.", response);
    }

    @Test
    public void inexistentProfile() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, UserDBManager.getCurrentNonce(TestUser),"blah", simOperator, simOperatorName, simSerialNumber, simCountryIso, imsiNumber));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 409);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("No matching profile.", response);
    }

    @Test
    public void nullUsername() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(null, UserDBManager.getCurrentNonce(TestUser), imei, simOperator, simOperatorName, simSerialNumber, simCountryIso, imsiNumber));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong json keys.", response);
    }

    @Test
    public void nullNonce() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, null, imei, simOperator, simOperatorName, simSerialNumber, simCountryIso, imsiNumber));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong json keys.", response);
    }

    @Test
    public void nullImei() throws Exception {
        String json = createJson(TestUser, UserDBManager.getCurrentNonce(TestUser), null, simOperator, simOperatorName, simSerialNumber, simCountryIso, imsiNumber);
        nullKeys(json);
    }

    @Test
    public void nullSimOperator() throws Exception {
        String json = createJson(TestUser, UserDBManager.getCurrentNonce(TestUser), imei, null, simOperatorName, simSerialNumber, simCountryIso, imsiNumber);
        nullKeys(json);
    }

    @Test
    public void nullSimOperatorName() throws Exception {
        String json = createJson(TestUser, UserDBManager.getCurrentNonce(TestUser), imei, simOperator, null, simSerialNumber, simCountryIso, imsiNumber);
        nullKeys(json);
    }

    @Test
    public void nullSimSerialNumber() throws Exception {
        String json = createJson(TestUser, UserDBManager.getCurrentNonce(TestUser), imei, simOperator, simOperatorName, null, simCountryIso, imsiNumber);
        nullKeys(json);
    }

    @Test
    public void nullSimCountryIso() throws Exception {
        String json = createJson(TestUser, UserDBManager.getCurrentNonce(TestUser), imei, simOperator, simOperatorName, simSerialNumber, null, imsiNumber);
        nullKeys(json);
    }
    
    @Test
    public void nullImsiNumber() throws Exception {
        String json = createJson(TestUser, UserDBManager.getCurrentNonce(TestUser), imei, simOperator, simOperatorName, simSerialNumber, simCountryIso, null);
        nullKeys(json);
    }
}
