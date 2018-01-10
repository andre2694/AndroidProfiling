package utils;

import dbutils.DatabaseManager;
import models.Profile;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ServerTesting {

    public static SecretKey aesKey;
    public static String imei = "imei";
    public static String softwareVersion = "softwareversion";
    public static String simCountryIso = "simcountryiso";
    public static String simOperator = "simoperator";
    public static String simOperatorName = "simoperatorname";
    public static String simSerialNumber = "simserial";
    public static String imsiNumber = "imsinumber";
    public static String macAddress = "macaddress";
    public static String ipAddress = "ipaddress";
    public static ArrayList<String> networksSSID;
    public static String network = "network";
    public static String network2 = "network2";
    public static String osVersion = "osversion";
    public static String sdkVersion = "sdkversion";
    public static ArrayList<String> googleAccounts;
    public static String google = "google";
    public static ArrayList<String> memorizedAccounts;
    public static String account = "account";
    public static String account2 = "account2";
    public static String deviceName = "devicename";
    public static String screenResolution = "screenresolution";
    public static String keyboardLanguage = "keyboardlanguage";
    public static ArrayList<String> inputMethods;
    public static String input = "input";
    public static String input2 = "input2";
    public static ArrayList<String> installedApplications;
    public static String applications = "applications";
    public static String applications2 = "applications2";
    public static Profile profile;
    public static String softwareVersion2 = "softwareversion2";
    public static String simCountryIso2 = "simcountryiso2";
    public static String simOperator2 = "simoperator2";
    public static String simOperatorName2 = "simoperatorname2";
    public static String simSerialNumber2 = "simserial2";
    public static String imsiNumber2 = "imsinumber2";
    public static String macAddress2 = "macaddress2";
    public static String ipAddress2 = "ipaddress2";

    public static String hmac(SecretKey key, String value) {
        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            sha256_HMAC.init(key);
            return new BASE64Encoder().encode(sha256_HMAC.doFinal(value.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Initializes the lists, and profiles
     */
    public static void start() {
        try {
            aesKey = DatabaseManager.generateAesKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        networksSSID = new ArrayList<String>();
        googleAccounts = new ArrayList<String>();
        memorizedAccounts = new ArrayList<String>();
        inputMethods = new ArrayList<String>();
        installedApplications = new ArrayList<String>();
        // Insert on lists
        networksSSID.add(hmac(aesKey, network));
        networksSSID.add(hmac(aesKey, network2));
        networksSSID.add(hmac(aesKey, "A"));
        networksSSID.add(hmac(aesKey, "B"));
        networksSSID.add(hmac(aesKey, "C"));
        networksSSID.add(hmac(aesKey, "D"));
        googleAccounts.add(hmac(aesKey, google));
        memorizedAccounts.add(hmac(aesKey, account));
        memorizedAccounts.add(hmac(aesKey, account2));
        memorizedAccounts.add(hmac(aesKey, "A"));
        memorizedAccounts.add(hmac(aesKey, "B"));
        inputMethods.add(hmac(aesKey, input));
        inputMethods.add(hmac(aesKey, input2));
        installedApplications.add(hmac(aesKey, applications));
        installedApplications.add(hmac(aesKey, applications2));
        installedApplications.add(hmac(aesKey, "A"));
        installedApplications.add(hmac(aesKey, "B"));
        installedApplications.add(hmac(aesKey, "C"));
        installedApplications.add(hmac(aesKey, "D"));
        installedApplications.add(hmac(aesKey, "E"));
        profile = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
    }
}
