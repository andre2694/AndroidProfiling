package server;

import dbutils.AuthUserDBManager;
import dbutils.UserDBManager;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.ServerTesting;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;

public class CancelAccountHandlerTest extends PostHandlerTesting {

    @BeforeClass
    public static void setup() throws Exception {
        AuthUserDBManager.insertUser(TestUser, "password", "email");
        URL = "http://localhost:8001/cancel";
    }

    @AfterClass
    public static void clean() throws Exception {
        AuthUserDBManager.removeAllUsers();
    }


    public String createJson(String username, String pass) {
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
    public void successCancel() throws Exception {
        AuthUserDBManager.insertUser("User1", "password", "email");
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson("User1", "password"));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 200);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("User successfully removed.", response);
        Assert.assertFalse(UserDBManager.hasUser("User1"));
    }

    @Test
    public void wrongPassword() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, "WrongPassword"));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 409);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong password", response);
    }

    @Test
    public void nonexistentUser() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser+"1", UserPassword));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 409);

        String response = getPostResponseMessage(l);
        Assert.assertEquals("User Doest Not Exists", response);
    }

    @Test
    public void nullPassword() throws Exception {
        String json = createJson(TestUser, null);
        nullKeys(json);
    }

    @Test
    public void nullUsername() throws Exception {
        String json = createJson(null, UserPassword);
        nullKeys(json);
    }
}
