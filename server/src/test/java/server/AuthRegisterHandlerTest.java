package server;

import dbutils.AuthUserDBManager;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;

public class AuthRegisterHandlerTest extends PostHandlerTesting {

    @BeforeClass
    public static void setup() throws Exception {
        AuthUserDBManager.insertUser(TestUser, "password", "email");
        URL = "http://localhost:8001/register";
    }

    @AfterClass
    public static void tearDown() throws Exception {
        AuthUserDBManager.removeAllUsers();
    }

    public String createJsonRegister(String email, String pass, String username) {
        JSONObject jo = new JSONObject();
        jo.put("email", email);
        jo.put("password", pass);
        jo.put("username", username);
        return jo.toString();
    }

    @Test
    public void successRegister() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJsonRegister(UserEmail+".com", UserPassword, TestUser+"1"));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(201, responseCode);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("User created successfully.", response);
    }

    @Test
    public void userAlreadyExists() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJsonRegister(UserEmail, UserPassword, TestUser));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 409);
        String response = getPostResponseMessage(l);
        Assert.assertEquals(response,"Username already exists. Please chose a different username.");
    }

    @Test
    public void nullEmail() throws Exception {
        nullKeys(createJsonRegister(null, UserPassword, TestUser));
    }

    @Test
    public void nullPassword() throws Exception {
        nullKeys(createJsonRegister(UserEmail, null, TestUser));
    }

    @Test
    public void nullUsername() throws Exception {
        nullKeys(createJsonRegister(UserEmail, UserPassword, null));
    }
}
