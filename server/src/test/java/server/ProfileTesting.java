package server;

import dbutils.UserDBManager;
import models.Profile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import server.utils.ServerUtils;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ProfileTesting extends PostHandlerTesting {

    /**
     * Creates a JSON object with an username and a profile
     * @return the created JSON object
     */
    public String createJson(String username, String profile) {
        JSONObject jo = new JSONObject();
        jo.put("username", username);
        jo.put("profile", profile);
        try {
            jo.put("nonce", UserDBManager.getCurrentNonce(username));
        } catch (Exception e) {
            jo.put("nonce", "nonce123");
        }
        try {
            return URLEncoder.encode(jo.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Test
    public void nullProfile() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, null));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong json keys.", response);
    }

    @Test
    public void nullUsername() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(null, ""));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong json keys.", response);
    }

    public void inexistentProfile() throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, ServerUtils.exportProfile(profile2)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 409);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Profile does not exist.", response);
    }

    /**
     * Creates a POST request where one of the keys is missing and checks that the handler throws an appropriate exception
     * @param p the profile to be send within the POST request
     */
    public void wrongKeysTest(Profile p) throws Exception {
        java.net.URL url = new URL(URL);
        HttpURLConnection l = (HttpURLConnection) url.openConnection();
        createPostRequest(l, createJson(TestUser, exportProfile(p)));
        int responseCode = l.getResponseCode();
        Assert.assertEquals(responseCode, 400);
        String response = getPostResponseMessage(l);
        Assert.assertEquals("Wrong profile keys.", response);
    }

    /**
     * Exports a profile into a JSON string, however, unlike the exportProfile method in ServerUtils, this method ignores keys that have null values and empty lists.
     * With this method is possible to test requests with missing keys.
     * @param profile the profile to be send withing the request
     * @return a String that contains a JSON object representation of the given profile
     */
    public static String exportProfile(Profile profile) {
        JSONObject p = new JSONObject();
        JSONArray arr = new JSONArray();

        if (profile.getImei() != null)
            p.put("imei", profile.getImei());

        if (profile.getSoftwareVersion() != null)
            p.put("software_version", profile.getSoftwareVersion());

        if (profile.getSimCountryIso() != null)
            p.put("sim_country_iso", profile.getSimCountryIso());

        if (profile.getSimOperator() != null)
            p.put("sim_operator", profile.getSimOperator());

        if (profile.getSimOperatorName() != null)
            p.put("sim_operator_name", profile.getSimOperatorName());

        if (profile.getSimSerialNumber() != null)
            p.put("sim_serial_number", profile.getSimSerialNumber());

        if (profile.getImsiNumber() != null)
            p.put("imsi_number", profile.getImsiNumber());

        if (profile.getMacAddress() != null)
            p.put("mac_address", profile.getMacAddress());

        if (profile.getIpAddress() != null)
            p.put("ip_address", profile.getIpAddress());

        if (profile.getOsVersion() != null)
            p.put("os_version", profile.getOsVersion());

        if (profile.getSdkVersion() != null)
            p.put("sdk_version", profile.getSdkVersion());

        if (profile.getDeviceName() != null)
            p.put("device_name", profile.getDeviceName());

        if (profile.getScreenResolution() != null)
            p.put("screen_resolution", profile.getScreenResolution());

        if (profile.getKeyboardLanguage() != null)
            p.put("keyboard_language", profile.getKeyboardLanguage());

        for (String s: profile.getNetworksSSID()) {
            arr.put(s);
        }
        if (arr.length() != 0)
            p.put("memorized_networks", arr);

        arr = new JSONArray();
        for (String s: profile.getGoogleAccounts()) {
            arr.put(s);
        }
        if (arr.length() != 0)
            p.put("google_accounts", arr);

        arr = new JSONArray();
        for (String s: profile.getMemorizedAccounts()) {
            arr.put(s);
        }
        if (arr.length() != 0)
            p.put("memorized_accounts", arr);

        arr = new JSONArray();
        for (String s: profile.getInputMethods()) {
            arr.put(s);
        }
        if (arr.length() != 0)
            p.put("keyboards", arr);

        arr = new JSONArray();
        for (String s: profile.getInstalledApplications()) {
            arr.put(s);
        }
        if (arr.length() != 0)
            p.put("installed_applications", arr);

        return p.toString();
    }
}
