package server.utils;

import exceptions.InvalidProfileJSONException;
import models.Profile;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.security.*;
import java.util.ArrayList;

public class ServerUtils {
    public static double THRESHOLD = 0.75;
    public static double ACCOUNTS_THRESHOLD = 0.66;
    public static boolean SERVER_DEBUG = true;
    public static boolean SERVER_DEPLOY = true;
    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    public static void loadKeys() {
        try {
            char[] password = "password".toCharArray();
            String alias = "selfsigned";
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("testkey.jks");
            ks.load(fis, password);
            privateKey = (PrivateKey) ks.getKey(alias, password);
            publicKey = ks.getCertificate(alias).getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a json object with profile information, creating a new Profile instance with the parsed information
     * @param jsonProfile the json object to be parsed
     * @return a new Profile instance
     */
    public static Profile parseProfile(String jsonProfile) {
        try {
            JSONObject p = new JSONObject(jsonProfile);
            ArrayList<String> stringArgs = new ArrayList<String>();
            ArrayList<JSONArray> arrayArgs = new ArrayList<JSONArray>();

            // Gets all the keys
            stringArgs.add((String) p.get("imei"));
            stringArgs.add((String) p.get("software_version"));
            stringArgs.add((String) p.get("sim_country_iso"));
            stringArgs.add((String) p.get("sim_operator"));
            stringArgs.add((String) p.get("sim_operator_name"));
            stringArgs.add((String) p.get("sim_serial_number"));
            stringArgs.add((String) p.get("imsi_number"));
            stringArgs.add((String) p.get("mac_address"));
            stringArgs.add((String) p.get("ip_address"));
            arrayArgs.add((JSONArray) p.get("memorized_networks"));
            stringArgs.add((String) p.get("os_version"));
            stringArgs.add((String) p.get("sdk_version"));
            arrayArgs.add((JSONArray) p.get("google_accounts"));
            arrayArgs.add((JSONArray) p.get("memorized_accounts"));
            stringArgs.add((String) p.get("device_name"));
            stringArgs.add((String) p.get("screen_resolution"));
            stringArgs.add((String) p.get("keyboard_language"));
            arrayArgs.add((JSONArray) p.get("keyboards"));
            arrayArgs.add((JSONArray) p.get("installed_applications"));

            // Parses the json arrays
            ArrayList<String> networksSSID = new ArrayList<String>();
            ArrayList<String> googleAccounts = new ArrayList<String>();
            ArrayList<String> memorizedAccounts = new ArrayList<String>();
            ArrayList<String> inputMethods = new ArrayList<String>();
            ArrayList<String> installedApplications = new ArrayList<String>();
            for(int i = 0; i < arrayArgs.get(0).length(); i++){
                networksSSID.add((String) arrayArgs.get(0).get(i));
            }
            for(int i = 0; i < arrayArgs.get(1).length(); i++){
                googleAccounts.add((String) arrayArgs.get(1).get(i));
            }
            for(int i = 0; i < arrayArgs.get(2).length(); i++){
                memorizedAccounts.add((String) arrayArgs.get(2).get(i));
            }
            for(int i = 0; i < arrayArgs.get(3).length(); i++){
                inputMethods.add((String) arrayArgs.get(3).get(i));
            }
            for(int i = 0; i < arrayArgs.get(4).length(); i++){
                installedApplications.add((String) arrayArgs.get(4).get(i));
            }

            return new Profile(stringArgs.get(0), stringArgs.get(1), stringArgs.get(2), stringArgs.get(3),
                    stringArgs.get(4), stringArgs.get(5), stringArgs.get(6), stringArgs.get(7), stringArgs.get(8), networksSSID,
                    stringArgs.get(9), stringArgs.get(10), googleAccounts, memorizedAccounts, stringArgs.get(11),
                    stringArgs.get(12), stringArgs.get(13), inputMethods, installedApplications);
          } catch (Exception e) {
            if(SERVER_DEBUG) e.printStackTrace();
            throw new InvalidProfileJSONException();
        }
    }

    /**
     * Exports a Profile instance as a String that contains a JSON object
     * @param profile the profile to be exported
     * @return a String that contains a JSON object with all the profile information
     */
    public static String exportProfile(Profile profile) {
        JSONObject p = new JSONObject();
        JSONArray arr = new JSONArray();
        p.put("imei", profile.getImei());
        p.put("software_version", profile.getSoftwareVersion());
        p.put("sim_country_iso", profile.getSimCountryIso());
        p.put("sim_operator", profile.getSimOperator());
        p.put("sim_operator_name", profile.getSimOperatorName());
        p.put("sim_serial_number", profile.getSimSerialNumber());
        p.put("imsi_number", profile.getImsiNumber());
        p.put("mac_address", profile.getMacAddress());
        p.put("ip_address", profile.getIpAddress());
        p.put("os_version", profile.getOsVersion());
        p.put("sdk_version", profile.getSdkVersion());
        p.put("device_name", profile.getDeviceName());
        p.put("screen_resolution", profile.getScreenResolution());
        p.put("keyboard_language", profile.getKeyboardLanguage());

        for (String s: profile.getNetworksSSID()) {
            arr.put(s);
        }
        p.put("memorized_networks", arr);
        arr = new JSONArray();
        for (String s: profile.getGoogleAccounts()) {
            arr.put(s);
        }
        p.put("google_accounts", arr);
        arr = new JSONArray();
        for (String s: profile.getMemorizedAccounts()) {
            arr.put(s);
        }
        p.put("memorized_accounts", arr);
        arr = new JSONArray();
        for (String s: profile.getInputMethods()) {
            arr.put(s);
        }
        p.put("keyboards", arr);
        arr = new JSONArray();
        for (String s: profile.getInstalledApplications()) {
            arr.put(s);
        }
        p.put("installed_applications", arr);

        return p.toString();
    }
}
