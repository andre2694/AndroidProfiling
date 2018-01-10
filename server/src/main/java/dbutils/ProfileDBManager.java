package dbutils;

import exceptions.ProfileDoesNotExistsException;
import models.Profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProfileDBManager extends DatabaseManager {

    /* Insertion Methods */

    /**
     * Insert a profile in the database
     * @param profile the profile to be inserted
     * @param username the username of the owner of the profile
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void insertProfile(Profile profile, String username) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        PreparedStatement st = con.prepareStatement("INSERT INTO Profiles(IMEI, SOFTWAREVERSION, SIMCOUNTRYISO,  SIMOPERATOR," +
                "SIMOPERATORNAME, SIMSERIALNUMBER, IMSINUMBER, MACADDRESS, IPADDRESS, OSVERSION, SDKVERSION, DEVICENAME, SCREENRESOLUTION, KEYBOARDLANGUAGE, USERNAME)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        st.setString(1, profile.getImei());
        st.setString(2, profile.getSoftwareVersion());
        st.setString(3, profile.getSimCountryIso());
        st.setString(4, profile.getSimOperator());
        st.setString(5, profile.getSimOperatorName());
        st.setString(6, profile.getSimSerialNumber());
        st.setString(7, profile.getImsiNumber());
        st.setString(8, profile.getMacAddress());
        st.setString(9, profile.getIpAddress());
        st.setString(10, profile.getOsVersion());
        st.setString(11, profile.getSdkVersion());
        st.setString(12, profile.getDeviceName());
        st.setString(13, profile.getScreenResolution());
        st.setString(14, profile.getKeyboardLanguage());
        st.setString(15, username);
        st.execute();
        st.close();
        endTransaction(con);
        con.close();
        insertNetworks(profile.getImei(), profile.getNetworksSSID());
        insertGoogleAccounts(profile.getImei(), profile.getGoogleAccounts());
        insertAccounts(profile.getImei(), profile.getMemorizedAccounts());
        insertInputs(profile.getImei(), profile.getInputMethods());
        insertApplications(profile.getImei(), profile.getInstalledApplications());
    }

    /**
     * Inserts the list of the networks of a given profile in the database
     * @param imei the imei of the corresponding profile
     * @param networks the list of networks
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static void insertNetworks(String imei, ArrayList<String> networks) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        PreparedStatement st = con.prepareStatement("INSERT INTO NetworksSSID (NETWORK, IMEI) VALUES (?, ?)");
        for (String network: networks) {
            st.setString(1, network);
            st.setString(2, imei);
            st.execute();
        }
        st.close();
        endTransaction(con);
        con.close();
    }

    /**
     * Inserts the list of memorized google accounts of a given profile in the database
     * @param imei the imei of the corresponding profile
     * @param google the list of the memorized google accounts
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static void insertGoogleAccounts(String imei, ArrayList<String> google) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        PreparedStatement st = con.prepareStatement("INSERT INTO GoogleAccounts (GOOGLEACCOUNT, IMEI) VALUES (?, ?)");
        for (String g: google) {
            st.setString(1, g);
            st.setString(2, imei);
            st.execute();
        }
        st.close();
        endTransaction(con);
        con.close();
    }

    /**
     * Inserts the list of memorized accounts of a given profile in the database
     * @param imei the imei of the corresponding profile
     * @param accounts the list of memorized accounts
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static void insertAccounts(String imei, ArrayList<String> accounts) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        PreparedStatement st = con.prepareStatement("INSERT INTO MemorizedAccounts (ACCOUNT, IMEI) VALUES (?, ?)");
        for (String account: accounts) {
            st.setString(1, account);
            st.setString(2, imei);
            st.execute();
        }
        st.close();
        endTransaction(con);
        con.close();
    }

    /**
     * Inserts the list of the stored input methods of a given profile in the database
     * @param imei the imei of the corresponding profile
     * @param inputs the list of input methods of the smartphone
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static void insertInputs(String imei, ArrayList<String> inputs) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        PreparedStatement st = con.prepareStatement("INSERT INTO InputMethods (INPUT, IMEI) VALUES (?, ?)");
        for (String input: inputs) {
            st.setString(1, input);
            st.setString(2, imei);
            st.execute();
        }
        st.close();
        endTransaction(con);
        con.close();
    }

    /**
     * Inserts the list of installed applications of a given profile in the database
     * @param imei the imei of the corresponding profile
     * @param apps the list of installed applications
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static void insertApplications(String imei, ArrayList<String> apps) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        PreparedStatement st = con.prepareStatement("INSERT INTO InstalledApplications (APPLICATION, IMEI) VALUES (?, ?)");
        for (String app: apps) {
            st.setString(1, app);
            st.setString(2, imei);
            st.execute();
        }
        st.close();
        endTransaction(con);
        con.close();
    }

    /* Retrieve Methods */

    /**
     * Retrieve a stored profile given the profile's imei
     * @param imei the imei of the profile to be retrieved
     * @param username the username of the user
     * @return the Profile instance that matches the given imei
     * @throws ProfileDoesNotExistsException if the given imei doesn't match a stored profile
     */
    public static Profile getProfile(String imei, String username) throws Exception {
        Connection con = startConnection();
        try {
            beginTransaction(con);
            // Check for trajectories with the matching id
            PreparedStatement st = con.prepareStatement("select * from Profiles where IMEI = ? AND USERNAME = ?");
            st.setString(1, imei);
            st.setString(2, username);
            ResultSet set = st.executeQuery();
            set.next();
            String swversion = set.getString("SOFTWAREVERSION");
            String simcountryiso = set.getString("SIMCOUNTRYISO");
            String simoperator = set.getString("SIMOPERATOR");
            String simoperatorname = set.getString("SIMOPERATORNAME");
            String simserialnumber = set.getString("SIMSERIALNUMBER");
            String imsinumber = set.getString("IMSINUMBER");
            String macaddress = set.getString("MACADDRESS");
            String ipaddress = set.getString("IPADDRESS");
            String osversion = set.getString("OSVERSION");
            String sdkversion = set.getString("SDKVERSION");
            String devicename = set.getString("DEVICENAME");
            String resolution = set.getString("SCREENRESOLUTION");
            String keyboard = set.getString("KEYBOARDLANGUAGE");
            set.close();
            endTransaction(con);
            con.close();
            st.close();
            ArrayList<String> networks = getNetworks(imei);
            ArrayList<String> googleaccs = getGoogle(imei);
            ArrayList<String> accs = getAccounts(imei);
            ArrayList<String> inputs = getInputs(imei);
            ArrayList<String> apps = getApplications(imei);
            return new Profile(imei, swversion, simcountryiso, simoperator, simoperatorname, simserialnumber, imsinumber, macaddress, ipaddress,
                    networks, osversion, sdkversion, googleaccs, accs, devicename, resolution, keyboard, inputs, apps);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw new ProfileDoesNotExistsException();
        }
    }

    /**
     * Returns a boolean that says whether a given imei matches a stored profile or not
     * @param imei the imei to be checked
     * @return true if a profile exists, false otherwise
     */
    public static boolean hasProfile(String imei) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        PreparedStatement st = con.prepareStatement("SELECT * FROM Profiles WHERE IMEI = ?");
        st.setString(1, imei);

        boolean ret = st.executeQuery().next();
        st.close();
        endTransaction(con);
        con.close();
        return ret;
    }

    /**
     * Retrieves the list of networks of a given profile
     * @param imei the imei of the corresponding profile
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static ArrayList<String> getNetworks(String imei) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        // Check for trajectories with the matching id
        PreparedStatement st = con.prepareStatement("select * from NetworksSSID where IMEI = ?");
        st.setString(1, imei);

        ResultSet set = st.executeQuery();
        // Iterate through the ResultSet to parse the trajectories
        ArrayList<String> ret = new ArrayList<String>();
        while (set.next()) {
            ret.add(set.getString("NETWORK"));
        }

        set.close();
        endTransaction(con);
        con.close();
        st.close();
        return ret;
    }

    /**
     * Retrieves the list of google accounts of a given profile
     * @param imei the imei of the corresponding profile
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static ArrayList<String> getGoogle(String imei) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        // Check for trajectories with the matching id
        PreparedStatement st = con.prepareStatement("select * from GoogleAccounts where IMEI = ?");
        st.setString(1, imei);

        ResultSet set = st.executeQuery();
        // Iterate through the ResultSet to parse the trajectories
        ArrayList<String> ret = new ArrayList<String>();
        while (set.next()) {
            ret.add(set.getString("GOOGLEACCOUNT"));
        }

        set.close();
        endTransaction(con);
        con.close();
        st.close();
        return ret;
    }

    /**
     * Retrieves the list of memorized accounts of a given profile
     * @param imei the imei of the corresponding profile
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static ArrayList<String> getAccounts(String imei) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        // Check for trajectories with the matching id
        PreparedStatement st = con.prepareStatement("select * from MemorizedAccounts where IMEI = ?");
        st.setString(1, imei);

        ResultSet set = st.executeQuery();
        // Iterate through the ResultSet to parse the trajectories
        ArrayList<String> ret = new ArrayList<String>();
        while (set.next()) {
            ret.add(set.getString("ACCOUNT"));
        }

        set.close();
        endTransaction(con);
        con.close();
        st.close();
        return ret;
    }

    /**
     * Retrieves the list of memorized input methods of a given profile
     * @param imei the imei of the corresponding profile
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static ArrayList<String> getInputs(String imei) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        // Check for trajectories with the matching id
        PreparedStatement st = con.prepareStatement("select * from InputMethods where IMEI = ?");
        st.setString(1, imei);

        ResultSet set = st.executeQuery();
        // Iterate through the ResultSet to parse the trajectories
        ArrayList<String> ret = new ArrayList<String>();
        while (set.next()) {
            ret.add(set.getString("INPUT"));
        }

        set.close();
        endTransaction(con);
        con.close();
        st.close();
        return ret;
    }

    /**
     * Retrieves the list of installed applications of a given profile
     * @param imei the imei of the corresponding profile
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static ArrayList<String> getApplications(String imei) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        // Check for trajectories with the matching id
        PreparedStatement st = con.prepareStatement("select * from InstalledApplications where IMEI = ?");
        st.setString(1, imei);

        ResultSet set = st.executeQuery();
        // Iterate through the ResultSet to parse the trajectories
        ArrayList<String> ret = new ArrayList<String>();
        while (set.next()) {
            ret.add(set.getString("APPLICATION"));
        }

        set.close();
        endTransaction(con);
        con.close();
        st.close();
        return ret;
    }

    /* Delete stored information */
    /**
     * Removes a given profile
     * @param imei the imei of the corresponding profile
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void removeProfile(String imei) throws Exception {
        Connection connection = startConnection();
        beginTransaction(connection);
        PreparedStatement query = connection.prepareStatement("DELETE FROM Profiles WHERE IMEI = ?");
        query.setString(1, imei);
        query.execute();
        query.close();
        endTransaction(connection);
        connection.close();
    }

    /**
     * Removes all existing profiles, this method is used only in unit tests
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void removeAllProfiles() throws Exception {
        Connection connection = startConnection();
        beginTransaction(connection);
        PreparedStatement query = connection.prepareStatement("DELETE FROM Profiles WHERE TRUE");
        query.execute();
        query.close();
        endTransaction(connection);
        connection.close();
    }

    public static void removeInputMethods(String imei) throws Exception {
        Connection connection = startConnection();
        beginTransaction(connection);
        PreparedStatement query = connection.prepareStatement("DELETE FROM InputMethods WHERE IMEI = ?");
        query.setString(1, imei);
        query.execute();
        query.close();
        endTransaction(connection);
        connection.close();
    }

    public static void removeGoogleAccounts(String imei) throws Exception {
        Connection connection = startConnection();
        beginTransaction(connection);
        PreparedStatement query = connection.prepareStatement("DELETE FROM GoogleAccounts WHERE IMEI = ?");
        query.setString(1, imei);
        query.execute();
        query.close();
        endTransaction(connection);
        connection.close();
    }

    public static void removeAccounts(String imei) throws Exception {
        Connection connection = startConnection();
        beginTransaction(connection);
        PreparedStatement query = connection.prepareStatement("DELETE FROM MemorizedAccounts WHERE IMEI = ?");
        query.setString(1, imei);
        query.execute();
        query.close();
        endTransaction(connection);
        connection.close();
    }

    public static void removeApplications(String imei) throws Exception {
        Connection connection = startConnection();
        beginTransaction(connection);
        PreparedStatement query = connection.prepareStatement("DELETE FROM InstalledApplications WHERE IMEI = ?");
        query.setString(1, imei);
        query.execute();
        query.close();
        endTransaction(connection);
        connection.close();
    }

    public static void removeNetworks(String imei) throws Exception {
        Connection connection = startConnection();
        beginTransaction(connection);
        PreparedStatement query = connection.prepareStatement("DELETE FROM NetworksSSID WHERE IMEI = ?");
        query.setString(1, imei);
        query.execute();
        query.close();
        endTransaction(connection);
        connection.close();
    }


    /* Update stored information */

    /**
     * Updates the list of input methods of a given profile
     * @param imei the imei of the corresponding profile
     * @param inputMethods the list of input methods to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateInputMethods(String imei, ArrayList<String> inputMethods) throws Exception {
        removeInputMethods(imei);
        insertInputs(imei, inputMethods);
    }

    /**
     * Updates the list of google accounts of a given profile
     * @param imei the imei of the corresponding profile
     * @param googleAccounts the list of google accounts to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateGoogleAccounts(String imei, ArrayList<String> googleAccounts) throws Exception {
        removeGoogleAccounts(imei);
        insertGoogleAccounts(imei, googleAccounts);
    }

    /**
     * Updates the list of memorized accounts of a given profile
     * @param imei the imei of the corresponding profile
     * @param memorizedAccounts the list of memorized accounts to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateMemorizedAccounts(String imei, ArrayList<String> memorizedAccounts) throws Exception {
        removeAccounts(imei);
        insertAccounts(imei, memorizedAccounts);
    }

    /**
     * Updates the list of installed applications of a given profile
     * @param imei the imei of the corresponding profile
     * @param installedApplications the list of installed applications to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateInstalledApplications(String imei, ArrayList<String> installedApplications) throws Exception {
        removeApplications(imei);
        insertApplications(imei, installedApplications);
    }

    /**
     * Updates the list of memorized networks of a given profile
     * @param imei the imei of the corresponding profile
     * @param networksSSID the list of memorized networks to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateNetworksSSID(String imei, ArrayList<String> networksSSID) throws Exception {
        removeNetworks(imei);
        insertNetworks(imei, networksSSID);
    }

    /**
     * Updates the value of a static attribute
     * @param imei the imei of the corresponding profile
     * @param variable the name of the database column that corresponds to the given static attribute
     * @param value the new value of the static attribute
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    private static void updateStaticAttributes(String imei, String variable, String value) throws Exception {
        Connection con = startConnection();
        if (hasProfile(imei)) {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("UPDATE Profiles SET " + variable + " = ? WHERE IMEI = ?");
            st.setString(1, value);
            st.setString(2, imei);
            st.executeUpdate();

            st.close();
            endTransaction(con);
            con.close();
        } else {
            con.close();
            throw new ProfileDoesNotExistsException();
        }
    }

    /**
     * Updates the ip address of a given profile
     * @param imei the imei of the corresponding profile
     * @param ipAddress the ip address to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateIpAddress(String imei, String ipAddress) throws Exception {
        updateStaticAttributes(imei, "IPADDRESS", ipAddress);
    }

    /**
     * Updates the keyboard language of a given profile
     * @param imei the imei of the corresponding profile
     * @param keyboardLanguage the keyboard language to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateKeyboardLanguage(String imei, String keyboardLanguage) throws Exception {
        updateStaticAttributes(imei, "KEYBOARDLANGUAGE", keyboardLanguage);
    }

    /**
     * Updates the sim country iso of a given profile
     * @param imei the imei of the corresponding profile
     * @param simCountryIso the sim country iso to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateSimCountryIso(String imei, String simCountryIso) throws Exception {
        updateStaticAttributes(imei, "SIMCOUNTRYISO", simCountryIso);
    }

    /**
     * Updates the sim operator of a given profile
     * @param imei the imei of the corresponding profile
     * @param simOperator the sim operator to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateSimOperator(String imei, String simOperator) throws Exception {
        updateStaticAttributes(imei, "SIMOPERATOR", simOperator);
    }

    /**
     * Updates the sim serial number of a given profile
     * @param imei the imei of the corresponding profile
     * @param simSerialNumber the sim serial number to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateSimSerialNumber(String imei, String simSerialNumber) throws Exception {
        updateStaticAttributes(imei, "SIMSERIALNUMBER", simSerialNumber);
    }

    /**
     * Updates the sim operator name of a given profile
     * @param imei the imei of the corresponding profile
     * @param simOperatorName the sim operator name to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateSimOperatorName(String imei, String simOperatorName) throws Exception {
        updateStaticAttributes(imei, "SIMOPERATORNAME", simOperatorName);
    }

    /**
     * Updates the os version of a given profile
     * @param imei the imei of the corresponding profile
     * @param osVersion the os version to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateOsVersion(String imei, String osVersion) throws Exception {
        updateStaticAttributes(imei, "OSVERSION", osVersion);
    }

    /**
     * Updates the sdk version of a given profile
     * @param imei the imei of the corresponding profile
     * @param sdkVersion the sdk version to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateSdkVersion(String imei, String sdkVersion) throws Exception {
        updateStaticAttributes(imei, "SDKVERSION", sdkVersion);
    }

    /**
     * Updates the imsi number of a given profile
     * @param imei the imei of the corresponding profile
     * @param imsi the imsi number to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateImsiNumber(String imei, String imsi) throws Exception {
        updateStaticAttributes(imei, "IMSINUMBER", imsi);
    }

    /**
     * Updates the device name of a given profile
     * @param imei the imei of the corresponding profile
     * @param deviceName the device name to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateDeviceName(String imei, String deviceName) throws Exception {
        updateStaticAttributes(imei, "DEVICENAME", deviceName);
    }

    /**
     * Updates the software version of a given profile
     * @param imei the imei of the corresponding profile
     * @param softwareVersion the software version to be updated
     * @throws Exception if the connection to the database fails or if the transaction fails
     */
    public static void updateSoftwareVersion(String imei, String softwareVersion) throws Exception {
        updateStaticAttributes(imei, "SOFTWAREVERSION", softwareVersion);
    }

    /**
     * Updates the columns that refer to SIM card information, in the case where the user has purchased a different SIM card
     * @param imei the imei of the corresponding profile
     * @param simoperator the sim operator of the new sim card
     * @param simserialnumber the sim serial number of the new sim card
     * @param simcountry the sim country iso of the new sim card
     * @param simoperatorname the name of the operator of the new sim card
     * @param imsi the imsi number of the new sim card
     * @throws ProfileDoesNotExistsException if the given imei doesn't match a stored profile
     */
    public static void updateNumber(String imei, String simoperator, String simserialnumber, String simcountry, String simoperatorname, String imsi) throws Exception {
        Connection con = startConnection();
        if (hasProfile(imei)) {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("UPDATE Profiles SET SIMOPERATOR = ?, SIMOPERATORNAME = ?, SIMCOUNTRYISO = ?, SIMSERIALNUMBER = ?, IMSINUMBER = ? WHERE IMEI = ?");
            st.setString(1, simoperator);
            st.setString(2, simoperatorname);
            st.setString(3, simcountry);
            st.setString(4, simserialnumber);
            st.setString(5, imsi);
            st.setString(6, imei);
            st.executeUpdate();

            st.close();
            endTransaction(con);
            con.close();
        } else {
            con.close();
            throw new ProfileDoesNotExistsException();
        }
    }
}
