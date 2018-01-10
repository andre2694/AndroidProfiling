package dbutils;


import models.Profile;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import server.utils.ServerUtils;
import utils.ServerTesting;

import java.util.ArrayList;

public class ProfileTest extends ServerTesting {

    @BeforeClass
    public static void start() {
        ServerTesting.start();
        ServerUtils.SERVER_DEPLOY = false;
    }

    @AfterClass
    public static void clean() {
        ServerUtils.SERVER_DEPLOY = true;
    }

    @Test
    public void compareL1GL2() {
        ArrayList<String> list1 = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        list1.add("A");
        list1.add("B");
        list1.add("C");
        list1.add("D");
        list1.add("E");
        list2.add("A");
        list2.add("B");
        list2.add("C");
        list2.add("D");
        Assert.assertTrue(Profile.compareLists(list1, list2, ServerUtils.THRESHOLD, "abc", "a"));
    }

    @Test
    public void compareL2GL1() {
        ArrayList<String> list1 = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        list1.add("A");
        list1.add("B");
        list1.add("C");
        list1.add("D");
        list2.add("A");
        list2.add("B");
        list2.add("C");
        list2.add("D");
        list2.add("E");
        Assert.assertTrue(Profile.compareLists(list1, list2, ServerUtils.THRESHOLD, "abc", "a"));
    }

    @Test
    public void sameProfile() {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        Assert.assertTrue(profile.verifyProfile(p));
    }

    @Test
    public void differentImei() {
        Profile p = new Profile(hmac(aesKey, "0"), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        Assert.assertFalse(profile.verifyProfile(p));
    }

    @Test
    public void differentMacAddress() {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, "0"), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        Assert.assertFalse(profile.verifyProfile(p));
    }

    @Test
    public void differentScreenResolution() {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, "0"), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        Assert.assertFalse(profile.verifyProfile(p));
    }

    @Test
    public void dynamicAttributes() {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, "0"),
                hmac(aesKey, "0"), hmac(aesKey, "0"), hmac(aesKey, "0"), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        Assert.assertFalse(profile.verifyProfile(p));
    }

    @Test
    public void dynamicList() {
        ArrayList<String> networks = new ArrayList<String>();
        networks.add(hmac(aesKey, network));
        networks.add(hmac(aesKey, network2));
        networks.add(hmac(aesKey, "F"));
        networks.add(hmac(aesKey, "G"));
        networks.add(hmac(aesKey, "H"));
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, "0"), hmac(aesKey, "0"),
                hmac(aesKey, "0"), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networks, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        Assert.assertFalse(profile.verifyProfile(p));
    }


    @Test
    public void successDynamicAttributes() {
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, "0"), hmac(aesKey, "0"),
                hmac(aesKey, "0"), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        Assert.assertTrue(profile.verifyProfile(p));
    }

    @Test
    public void successNetworks() {
        ArrayList<String> networks = new ArrayList<String>();
        networks.add(hmac(aesKey, network));
        networks.add(hmac(aesKey, network2));
        networks.add(hmac(aesKey, "A"));
        networks.add(hmac(aesKey, "B"));
        networks.add(hmac(aesKey, "C"));
        networks.add(hmac(aesKey, "D"));
        networks.add(hmac(aesKey, "E"));
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, "0"), hmac(aesKey, "0"),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networks, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        Assert.assertTrue(profile.verifyProfile(p));
    }

    @Test
    public void insuccessNetworks() {
        ArrayList<String> networks = new ArrayList<String>();
        networks.add(hmac(aesKey, network));
        networks.add(hmac(aesKey, network2));
        networks.add(hmac(aesKey, "F"));
        networks.add(hmac(aesKey, "G"));
        networks.add(hmac(aesKey, "H"));
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, "0"), hmac(aesKey, "0"),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networks, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
        Assert.assertFalse(profile.verifyProfile(p));
    }

    @Test
    public void useCase() {
        ArrayList<String> apps = new ArrayList<String>();
        ArrayList<String> ntwks = new ArrayList<String>();
        ArrayList<String> accs = new ArrayList<String>();
        for (int i = 0; i < 50; i++)
            apps.add(i+"");
        for (int i = 0; i < 25; i++)
            ntwks.add(i+"");
        for (int i = 0; i < 5; i++)
            accs.add(i+"");
        Profile p = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, "0"), hmac(aesKey, "0"),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                ntwks, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, accs, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, apps);
        // Fabricate the change, 40 + 5 applications, 20 networks and 5 + 1 accounts
        ArrayList<String> apps2 = new ArrayList<String>();
        ArrayList<String> ntwks2 = new ArrayList<String>();
        ArrayList<String> accs2 = new ArrayList<String>();
        for (int i = 0; i < 40; i++)
            apps2.add(i+"");
        for (int i = 0; i < 20; i++)
            ntwks2.add(i+"");
        for (int i = 0; i < 5; i++)
            accs2.add(i+"");
        apps2.add("A");
        apps2.add("B");
        apps2.add("C");
        apps2.add("D");
        apps2.add("E");
        accs2.add("A");
        Profile p2 = new Profile(hmac(aesKey, imei), hmac(aesKey, softwareVersion), hmac(aesKey, "0"), hmac(aesKey, "0"),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                ntwks2, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, accs2, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, apps2);
        Assert.assertTrue(p.verifyProfile(p2));
    }
}