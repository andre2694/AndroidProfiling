package dbutils;

import exceptions.ProfileDoesNotExistsException;
import models.Profile;
import org.junit.*;
import utils.ServerTesting;

import static dbutils.DatabaseManager.generatePublicKey;
import java.util.ArrayList;

public class ProfileDBManagerTest extends ServerTesting {

    private static String username = "Name";
    private static Profile profile2;

    @BeforeClass
    public static void create() throws Exception {
        ServerTesting.start();
        initLists();
        UserDBManager.insertUser(username, generatePublicKey(), aesKey);
    }

    @AfterClass
    public static void destroy() throws Exception {
        UserDBManager.removeUser(username);
        ProfileDBManager.removeAllProfiles();
    }

    @After
    public void eraseProfiles() throws Exception {
        ProfileDBManager.removeAllProfiles();
    }

    private static void initLists() {
        profile2 = new Profile(hmac(aesKey, imei + "1"), hmac(aesKey, softwareVersion), hmac(aesKey, simCountryIso), hmac(aesKey, simOperator),
                hmac(aesKey, simOperatorName), hmac(aesKey, simSerialNumber), hmac(aesKey, imsiNumber), hmac(aesKey, macAddress), hmac(aesKey, ipAddress),
                networksSSID, hmac(aesKey, osVersion), hmac(aesKey, sdkVersion), googleAccounts, memorizedAccounts, hmac(aesKey, deviceName),
                hmac(aesKey, screenResolution), hmac(aesKey, keyboardLanguage), inputMethods, installedApplications);
    }

    @Test
    public void successInsertProfile() throws Exception {
        ProfileDBManager.insertProfile(profile, username);
        Profile p = ProfileDBManager.getProfile(hmac(aesKey, imei), username);
        Assert.assertTrue(ProfileDBManager.hasProfile(hmac(aesKey, imei)));
        Assert.assertTrue(p.equals(profile));
    }

    @Test(expected = ProfileDoesNotExistsException.class)
    public void getProfileWrongImei() throws Exception {
        ProfileDBManager.getProfile("wrong", username);
    }
}
