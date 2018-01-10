package server;

import dbutils.ProfileDBManager;
import dbutils.UserDBManager;
import models.Profile;
import org.json.JSONObject;
import org.junit.*;
import server.utils.ServerUtils;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.ArrayList;

public class BootstrapHandlerTest extends ProfileTesting {
    private static PublicKey publicKey;

    public String createJson(String username, String profile) {
        JSONObject jo = new JSONObject();
        jo.put("username", username);
        jo.put("profile", profile);
        jo.put("public_key", new BASE64Encoder().encode(publicKey.getEncoded()));
        try {
            ServerUtils.loadKeys();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, ServerUtils.publicKey);
            jo.put("symmetric_key", new BASE64Encoder().encode(cipher.doFinal(aesKey.getEncoded())));
            return URLEncoder.encode(jo.toString(), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    @BeforeClass
    public static void setup() throws Exception {
        initLists();
        publicKey = generatePublicKey();
        URL = "http://localhost:8000/bootstrap";
        ServerUtils.SERVER_DEBUG = false;
    }

    @After
    public void destroy() throws Exception {
        UserDBManager.removeAllUsers();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        ProfileDBManager.removeAllProfiles();
        ServerUtils.SERVER_DEBUG = true;
    }

    @Test
    public void successBootstrap() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, ServerUtils.exportProfile(profile)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(201, responseCode);
        String response = getPostResponseMessage(l);
        JSONObject obj = new JSONObject(response);
        Assert.assertEquals("Bootstrap succeeded.", obj.getString("message"));
        Assert.assertNotNull(obj.getString("code"));
        Assert.assertTrue(ProfileDBManager.hasProfile(profile.getImei()));
        Profile p = ProfileDBManager.getProfile(profile.getImei(), TestUser);
        Assert.assertTrue(p.equals(profile));
    }

    @Test
    public void wrongProfileImei() throws Exception {
        Profile p = new Profile(null, softwareVersion, simCountryIso, simOperator, simOperatorName, simSerialNumber, imsiNumber, macAddress, ipAddress,
                networksSSID, osVersion, sdkVersion, googleAccounts, memorizedAccounts, deviceName, screenResolution, keyboardLanguage,
                inputMethods, installedApplications);
        wrongKeysTest(p);
    }

    @Test
    public void wrongProfileSWVersion() throws Exception {
        Profile p = new Profile(imei, null, simCountryIso, simOperator, simOperatorName, simSerialNumber, imsiNumber, macAddress, ipAddress,
                networksSSID, osVersion, sdkVersion, googleAccounts, memorizedAccounts, deviceName, screenResolution, keyboardLanguage,
                inputMethods, installedApplications);
        wrongKeysTest(p);
    }

    @Test
    public void wrongNetworks() throws Exception {
        Profile p = new Profile(imei, softwareVersion, simCountryIso, simOperator, simOperatorName, simSerialNumber, imsiNumber, macAddress, ipAddress,
                new ArrayList<String>(), osVersion, sdkVersion, googleAccounts, memorizedAccounts, deviceName, screenResolution, keyboardLanguage,
                inputMethods, installedApplications);
        wrongKeysTest(p);
    }

    @Test
    public void existentUser() throws Exception {
        UserDBManager.insertUser(TestUser, generatePublicKey(), aesKey);
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, ServerUtils.exportProfile(profile)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(409, responseCode);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("User Already Exists.", response);
    }
}
