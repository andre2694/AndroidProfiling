package server;

import dbutils.ProfileDBManager;
import dbutils.UserDBManager;
import models.Profile;
import org.json.JSONObject;
import org.junit.*;
import server.utils.ServerUtils;
import utils.ServerTesting;

import javax.crypto.SecretKey;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;

public class ProfileHandlerTest extends ProfileTesting {
    private static PublicKey publicKey;

    @BeforeClass
    public static void setup() throws Exception {
        publicKey = generatePublicKey();
        initLists();
        UserDBManager.insertUser(TestUser, publicKey, aesKey);
        UserDBManager.insertUser("wrong", publicKey, aesKey);
        URL = "http://localhost:8000/profile";
        ProfileDBManager.insertProfile(profile, TestUser);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        UserDBManager.removeAllUsers();
        ProfileDBManager.removeAllProfiles();
    }

    @Test
    public void successCheckProfiles() throws Exception {
        successVerification(profile);
    }

    @Test
    public void wrongUsername() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson("wrong", ServerUtils.exportProfile(profile)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 409);
        String response = getPostResponseMessage(l);
        JSONObject obj = new JSONObject(response);
        Assert.assertEquals("Incorrect profile.", obj.getString("message"));
        Assert.assertNotNull(obj.getString("code"));
    }

    @Test
    public void differentProfiles() throws Exception {
        testVerification(profile3);
    }

    @Test
    public void inexistentProfileTest() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, ServerUtils.exportProfile(profile2)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 409);
        String response = getPostResponseMessage(l);
        JSONObject obj = new JSONObject(response);
        Assert.assertEquals("Profile does not exist.", obj.getString("message"));
        Assert.assertNotNull(obj.getString("code"));
    }

    @Test
    public void wrongProfileImei() throws Exception {
        models.Profile p = new Profile(null, softwareVersion, simCountryIso, simOperator, simOperatorName, simSerialNumber, imsiNumber, macAddress, ipAddress,
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

    private void testVerification(Profile p) throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, ServerUtils.exportProfile(p)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 409);
        String response = getPostResponseMessage(l);
        JSONObject obj = new JSONObject(response);
        Assert.assertEquals("Incorrect profile.", obj.getString("message"));
        Assert.assertNotNull(obj.getString("code"));
    }

    private void successVerification(Profile p) throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, ServerUtils.exportProfile(p)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(201, responseCode);
        String response = getPostResponseMessage(l);
        JSONObject obj = new JSONObject(response);
        Assert.assertEquals("Correct profile.", obj.getString("message"));
        Assert.assertNotNull(obj.getString("nonce"));
    }

    @Test
    public void differentSWVersion() throws Exception {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, "0"), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        testVerification(p);
    }

    @Test
    public void differentSimCountryIso() throws Exception {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, "0"), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        testVerification(p);
    }

    @Test
    public void differentMacAddress() throws Exception {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, "0"), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        testVerification(p);
    }

    @Test
    public void differentScreenResolution() throws Exception {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, "0"), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        testVerification(p);
    }

    @Test
    public void dynamicAttributes() throws Exception {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, "0"),
                hmac(aesKey, "0"), hmac(aesKey, "0"), hmac(aesKey, "0"), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        testVerification(p);
    }

    @Test
    public void dynamicList() throws Exception {
        ArrayList<String> networks = new ArrayList<String>();
        networks.add(hmac(aesKey, network));
        networks.add(hmac(aesKey, network2));
        networks.add(hmac(aesKey, "F"));
        networks.add(hmac(aesKey, "G"));
        networks.add(hmac(aesKey, "H"));
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, "0"),
                hmac(aesKey, "0"), hmac(aesKey, "0"), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networks, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        testVerification(p);
    }

    @Test
    public void successDynamicAttributes() throws Exception {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, "0"),
                hmac(aesKey, "0"), hmac(aesKey, "0"), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        successVerification(p);
    }

    @Test
    public void successNetworks() throws Exception {
        ArrayList<String> networks = new ArrayList<String>();
        networks.add(hmac(aesKey, network));
        networks.add(hmac(aesKey, network2));
        networks.add(hmac(aesKey, "A"));
        networks.add(hmac(aesKey, "B"));
        networks.add(hmac(aesKey, "C"));
        networks.add(hmac(aesKey, "D"));
        networks.add(hmac(aesKey, "E"));
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, "0"),
                hmac(aesKey, "0"), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        successVerification(p);
    }

    @Test
    public void insuccessNetworks() throws Exception {
        ArrayList<String> networks = new ArrayList<String>();
        networks.add(hmac(aesKey, network));
        networks.add(hmac(aesKey, network2));
        networks.add(hmac(aesKey, "F"));
        networks.add(hmac(aesKey, "G"));
        networks.add(hmac(aesKey, "H"));
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, "0"),
                hmac(aesKey, "0"), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networks, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        testVerification(p);
    }
}
