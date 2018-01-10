package server;

import dbutils.ProfileDBManager;
import dbutils.UserDBManager;
import models.Profile;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import server.utils.ServerUtils;
import utils.ServerTesting;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.ArrayList;

public class FullUpdateHandlerTest extends ProfileTesting {
    private static PublicKey publicKey;

    private static Profile newprofile;
    private static ArrayList<String> networks;
    private static ArrayList<String> googlel;
    private static ArrayList<String> accs;
    private static ArrayList<String> inputl;
    private static ArrayList<String> apps;

    public String createJson(String username, String profile) {
        JSONObject jo = new JSONObject();
        jo.put("username", username);
        jo.put("profile", profile);
        try {
            return URLEncoder.encode(jo.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    @BeforeClass
    public static void setup() throws Exception {
        publicKey = generatePublicKey();
        ServerTesting.start();
        UserDBManager.insertUser(TestUser, publicKey, aesKey);
        URL = "http://localhost:8000/update";
        initLists();
        ProfileDBManager.insertProfile(profile, TestUser);
        networks = new ArrayList<String>();
        googlel = new ArrayList<String>();
        accs = new ArrayList<String>();
        inputl = new ArrayList<String>();
        apps = new ArrayList<String>();
        // Insert on lists
        networks.add(hmac(aesKey, "A"));
        networks.add(hmac(aesKey, "B"));
        networks.add(hmac(aesKey, "C"));
        networks.add(hmac(aesKey, "D"));
        googlel.add(hmac(aesKey, google));
        memorizedAccounts.add(hmac(aesKey, "A"));
        memorizedAccounts.add(hmac(aesKey, "B"));
        inputl.add(hmac(aesKey, input));
        inputl.add(hmac(aesKey, input2));
        apps.add(hmac(aesKey, "A"));
        apps.add(hmac(aesKey, "B"));
        apps.add(hmac(aesKey, "C"));
        apps.add(hmac(aesKey, "D"));
        apps.add(hmac(aesKey, "E"));
        newprofile = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion2), hmac(aesKey, simCountryIso2), hmac(aesKey, simOperator2),
                hmac(aesKey, simOperatorName2), hmac(aesKey, simSerialNumber2), hmac(aesKey, imsiNumber2), hmac(aesKey, macAddress2), hmac(aesKey, ipAddress2),
                networks, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googlel, accs, hmac(aesKey, deviceName), hmac(aesKey, screenResolution),
                hmac(aesKey, keyboardLanguage), inputl, apps);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        UserDBManager.removeAllUsers();
        ProfileDBManager.removeAllProfiles();
    }

    @Test
    public void success() throws Exception {
        Assert.assertTrue(ProfileDBManager.hasProfile(hmac(aesKey, imei)));
        Profile p = ProfileDBManager.getProfile(hmac(aesKey, imei), TestUser);
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, ServerUtils.exportProfile(newprofile)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(201, responseCode);
        String response = getPostResponseMessage(l);
        JSONObject obj = new JSONObject(response);
        Assert.assertEquals("Successfully updated.", obj.getString("message"));
        Assert.assertNotNull(obj.getString("nonce"));
        Assert.assertTrue(ProfileDBManager.hasProfile(newprofile.getImei()));
        Profile aux = ProfileDBManager.getProfile(hmac(aesKey, imei), TestUser);
        Assert.assertFalse(aux.verifyProfile(p));
        Assert.assertTrue(aux.equals(newprofile));
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
    public void inexistentUser() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson("none", ServerUtils.exportProfile(profile)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(409, responseCode);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("User does not exists.", response);
    }
}