package server;

import dbutils.UserDBManager;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;

public class ConfirmCodeHandlerTest extends PostHandlerTesting {

    private static PublicKey publicKey;

    public String createJson(String username, String code) throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("username", username);
        jo.put("code", code);
        return URLEncoder.encode(jo.toString(), "UTF-8");
    }

    @BeforeClass
    public static void setup() throws Exception {
        publicKey = generatePublicKey();
        UserDBManager.insertUser(TestUser, publicKey, generateAesKey());
        URL = "http://localhost:8000/code";
    }

    @AfterClass
    public static void tearDown() throws Exception {
        UserDBManager.removeAllUsers();
    }

    @Test
    public void success() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, UserDBManager.getCurrentCode(TestUser)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 200);
        String response = getPostResponseMessage(l);
        JSONObject obj = new JSONObject(response);
        Assert.assertEquals("Code successfully verified.", obj.getString("message"));
        Assert.assertNotNull(obj.getString("nonce"));
    }

    @Test
    public void wrongCode() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, "1234"));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong code.", response);
    }

    @Test
    public void wrongUser() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson("wrong", "1234"));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("User Doest Not Exists.", response);
    }

    @Test
    public void nullUser() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(null, "1234"));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong json keys.", response);
    }

    @Test
    public void nullCode() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, null));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong json keys.", response);
    }
}
