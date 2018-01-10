package server;

import dbutils.AuthUserDBManager;
import dbutils.UserDBManager;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.security.PublicKey;

public class LoginHandlerTest extends PostHandlerTesting {
    private static PublicKey publicKey;
    String WrongPassword = "wr0ng";

    @BeforeClass
    public static void setup() throws Exception {
        publicKey = generatePublicKey();
        AuthUserDBManager.insertUser(TestUser, UserPassword, UserEmail);
        UserDBManager.insertUser(TestUser, publicKey, generateAesKey());
        URL = "http://localhost:8001/login";
    }

    @AfterClass
    public static void tearDown() throws Exception {
        AuthUserDBManager.removeAllUsers();
        UserDBManager.removeAllUsers();
    }

    public String createJsonLogin(String username, String pass) {
        JSONObject jo = new JSONObject();
        jo.put("username", username);
        jo.put("password", pass);
        try {
            return URLEncoder.encode(jo.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Test
    public void successLogin() throws IOException {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJsonLogin(TestUser,UserPassword));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 200);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Successful login.", response);
    }

    @Test
    public void wrongPassword() throws IOException {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJsonLogin(TestUser,WrongPassword));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);

        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong password", response);
    }

    @Test
    public void passwordInjection() throws IOException {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJsonLogin(TestUser,"cenas or 1=1"));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);

        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong password", response);
    }

    @Test
    public void nonexistentUser() throws IOException {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJsonLogin(TestUser+"1",WrongPassword));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);

        String response = getPostResponseMessage(l);
        Assert.assertEquals("User Doest Not Exists", response);
    }

    @Test
    public void nullPassword() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJsonLogin(TestUser, null));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals(response,"Wrong json keys.");
    }

    @Test
    public void nullUsername() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJsonLogin(null, UserPassword));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals(response,"Wrong json keys.");
    }
}
