package models;

import dbutils.ProfileDBManager;
import server.utils.LogManager;
import server.utils.ServerUtils;

import java.util.ArrayList;

public class Profile {

    private String imei;
    private String softwareVersion;
    private String simCountryIso;
    private String simOperator;
    private String simOperatorName;
    private String simSerialNumber;
    private String imsiNumber;
    private String macAddress;
    private String ipAddress;
    private ArrayList<String> networksSSID;
    private String osVersion;
    private String sdkVersion;
    private ArrayList<String> googleAccounts;
    private ArrayList<String> memorizedAccounts;
    private String deviceName;
    private String screenResolution;
    private String keyboardLanguage;
    private ArrayList<String> inputMethods;
    private ArrayList<String> installedApplications;
    public String changedInformation;

    /**
     * Creates a new profile, given all the necessary attributes
     */
    public Profile(String imei, String softwareVersion, String simCountryIso, String simOperator, String simOperatorName, String simSerialNumber,
                   String imsiNumber, String macAddress, String ipAddress, ArrayList<String> networksSSID, String osVersion,
                   String sdkVersion, ArrayList<String> googleAccounts, ArrayList<String> memorizedAccounts, String deviceName,
                   String screenResolution, String keyboardLanguage, ArrayList<String> inputMethods, ArrayList<String> installedApplications) {

        this.imei = imei;
        this.softwareVersion = softwareVersion;
        this.simCountryIso = simCountryIso;
        this.simOperator = simOperator;
        this.simOperatorName = simOperatorName;
        this.simSerialNumber = simSerialNumber;
        this.imsiNumber = imsiNumber;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.networksSSID = networksSSID;
        this.osVersion = osVersion;
        this.sdkVersion = sdkVersion;
        this.googleAccounts = googleAccounts;
        this.memorizedAccounts = memorizedAccounts;
        this.deviceName = deviceName;
        this.screenResolution = screenResolution;
        this.keyboardLanguage = keyboardLanguage;
        this.inputMethods = inputMethods;
        this.installedApplications = installedApplications;
        this.changedInformation = "";
    }

    /* Getters */
    public String getImei() {
        return imei;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public String getSimCountryIso() {
        return simCountryIso;
    }

    public String getSimOperator() {
        return simOperator;
    }

    public String getSimOperatorName() {
        return simOperatorName;
    }

    public String getSimSerialNumber() {
        return simSerialNumber;
    }

    public String getImsiNumber() {
        return imsiNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public ArrayList<String> getNetworksSSID() {
        return networksSSID;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public ArrayList<String> getGoogleAccounts() {
        return googleAccounts;
    }

    public ArrayList<String> getMemorizedAccounts() {
        return memorizedAccounts;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    public String getKeyboardLanguage() {
        return keyboardLanguage;
    }

    public ArrayList<String> getInputMethods() {
        return inputMethods;
    }

    public ArrayList<String> getInstalledApplications() {
        return installedApplications;
    }

    /* Other methods */

    /**
     * Method that checks if two profiles are equal, comparing its attributes
     * @param p the other profile to be checked
     * @return true if both profiles are exactly the same, false otherwise
     */
    public boolean equals(Profile p) {
        if (this.imei.equals(p.getImei()) && this.softwareVersion.equals(p.getSoftwareVersion()) &&
                this.simCountryIso.equals(p.getSimCountryIso()) && this.simOperator.equals(p.getSimOperator()) &&
                this.simOperatorName.equals(p.getSimOperatorName()) && this.imsiNumber.equals(p.getImsiNumber()) &&
                this.macAddress.equals(p.getMacAddress()) && this.ipAddress.equals(p.getIpAddress()) &&
                this.networksSSID.equals(p.getNetworksSSID()) && this.osVersion.equals(p.getOsVersion()) &&
                this.sdkVersion.equals(p.getSdkVersion()) && this.googleAccounts.equals(p.getGoogleAccounts()) &&
                this.memorizedAccounts.equals(p.getMemorizedAccounts()) && this.deviceName.equals(p.getDeviceName()) &&
                this.screenResolution.equals(p.getScreenResolution()) && this.keyboardLanguage.equals(p.getKeyboardLanguage()) &&
                this.inputMethods.equals(p.getInputMethods()) && this.installedApplications.equals(p.getInstalledApplications()))
            return true;
        else
            return false;
    }

    public String toString() {
        return "IMEI: " + this.imei + " | Software Version: " + this.softwareVersion + " | Sim Country ISO: " + this.simCountryIso +
                " | Sim Operator: " + this.simOperator + " | Sim Operator Name: " + this.simOperatorName + " | Sim Serial Number: " + this.simSerialNumber +
                " | IMSI Number: " + this.imsiNumber + " | MAC Address: " + this.macAddress + " | IP Address: " + this.ipAddress +
                " | Networks: " + this.networksSSID.toString() + " | OS Version: " + this.osVersion + " | SDK Version: " + this.sdkVersion +
                " | Google Accounts: " + this.googleAccounts.toString() + " | Accounts: " + this.memorizedAccounts.toString() + " | Device Name: " + this.deviceName +
                " | Screen Resolution: " + this.screenResolution  + " | Keyboard Language: " + this.keyboardLanguage + " | Input Methods: " + this.inputMethods.toString() + " | Applications: " + this.installedApplications.toString();
    }

    /**
     * Checks if two profiles match each other, using a defined threshold
     * @param p the other profile to be checked
     * @return true if the two profiles match, false otherwise
     */
    public boolean verifyProfile(Profile p) {
        // If the two profiles are exactly the same, they will obviously match each other
        if (this.equals(p)) {
            if (ServerUtils.SERVER_DEPLOY) LogManager.getInstance().logInfo(this.getImei() + ": Equal Profile.");
            System.out.println("Equal profile.");
            changedInformation = "Nothing has changed.";
            return true;
        }

        // Check static attributes, if something is different the profile is invalid
        if (!imei.equals(p.getImei()) | !macAddress.equals(p.getMacAddress()) | !screenResolution.equals(p.getScreenResolution())) {
            if (ServerUtils.SERVER_DEPLOY) LogManager.getInstance().logError(this.getImei() + ": Static attribute changed.");
            System.out.println(this.getImei() + ": Static attribute changed.");
            changedInformation = "Static Attributes changed.";
            return false;
        }

        String logString = this.getImei() + ": ";
        // Comparing the number of changed attributes
        int changed = 0;

        if (!softwareVersion.equals(p.getSoftwareVersion())) {
            String message = " Software Version changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if (!simOperator.equals(p.getSimOperator())) {
            String message = " SIM Operator changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if (!simOperatorName.equals(p.getSimOperatorName())) {
            String message = " SIM Operator Name changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if (!simCountryIso.equals(p.getSimCountryIso())) {
            String message = " SIM Country Iso changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!simSerialNumber.equals(p.getSimSerialNumber())) {
            String message = " SIM Serial Number changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!imsiNumber.equals(p.getImsiNumber())) {
            String message = " IMSI Number changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if (!ipAddress.equals(p.getIpAddress())) {
            String message = " IP Address changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!osVersion.equals(p.getOsVersion())) {
            String message = " OS Version changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!sdkVersion.equals(p.getSdkVersion())) {
            String message = " SDK Version changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!deviceName.equals(p.getDeviceName())) {
            String message = " Device Name changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!keyboardLanguage.equals(p.getKeyboardLanguage())) {
            String message = " Keyboard Language changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!networksSSID.equals(p.getNetworksSSID())) {
            String message = " Networks changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!googleAccounts.equals(p.getGoogleAccounts())) {
            String message = " Google Accounts changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!memorizedAccounts.equals(p.getMemorizedAccounts())) {
            String message = " Memorized Accounts changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!inputMethods.equals(p.getInputMethods())) {
            String message = " Input Methods changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        if(!installedApplications.equals(p.getInstalledApplications())) {
            String message = " Installed Applications changed. ";
            logString = logString + message;
            changedInformation = changedInformation + message + "\n";
            changed++;
        }

        // Dividing the number of changed attributes by the number of checked attributes
        double division = changed/ (double) 16;
        // If the division is greater than the threshold, the two profiles mismatch as they are not similar enough
        if (division >= (1 - ServerUtils.THRESHOLD)) {
            if (ServerUtils.SERVER_DEPLOY) LogManager.getInstance().logError(logString);
            System.out.println("ERROR: " + logString);
            return false;
        }

        // Checks every list with the threshold
        if (ServerUtils.SERVER_DEPLOY) LogManager.getInstance().logInfo(logString);
        System.out.println("INFO: " + logString);
        return compareLists(networksSSID, p.getNetworksSSID(), ServerUtils.THRESHOLD, "NetworksSSID", this.imei) &&
                compareLists(googleAccounts, p.getGoogleAccounts(), ServerUtils.ACCOUNTS_THRESHOLD, "GoogleAccounts", this.imei) &&
                compareLists(memorizedAccounts, p.getMemorizedAccounts(), ServerUtils.ACCOUNTS_THRESHOLD, "Accounts", this.imei) &&
                compareLists(inputMethods, p.getInputMethods(), ServerUtils.THRESHOLD, "InputMethods", this.imei) &&
                compareLists(installedApplications, p.getInstalledApplications(), ServerUtils.THRESHOLD, "Applications", this.imei);
    }

    /**
     * Checks if two lists match, considering the defined threshold
     * @param list1 the first list to be compared
     * @param list2 the second list to be compared
     * @return true if the two lists match, false otherwise
     */
    public static boolean compareLists(ArrayList<String> list1, ArrayList<String> list2, double threshold, String listname, String imei) {
        // If the two lists are exactly the same, they will obviously match
        if (list1.equals(list2))
            return true;

        // Get a list with the common elements
        ArrayList<String> commonElements = new ArrayList<String>(list1);
        commonElements.retainAll(list2);

        // Calculate the minimum value of common elements based on the threshold
        double minimum = Math.round((1 - threshold) * list1.size());
        // Calculate the maximum value of different elements based on the threshold
        double maximum = Math.round((1 - threshold) * list2.size());
        // If the number of common elements is greater than the minimum and the number of changes is smaller than the maximum return true
        int changedElements = list2.size() - commonElements.size();
        String logString = imei + " - " + listname + ": " + " Common Elements - " + commonElements.size() + ". Changed Elements - " + changedElements;
        if (ServerUtils.SERVER_DEPLOY) LogManager.getInstance().logInfo(logString);
        System.out.println(logString);
        return ((minimum < commonElements.size()) && (changedElements <= maximum));
    }

    /**
     * Updating two profiles that match each other, this method will not be called if two profiles don't match
     * @param newProfile the most recent profile that matches the stored profile
     * @throws Exception if the connection to the database or the transaction fails
     */
    public void updateProfile(Profile newProfile) throws Exception {
        // If the two profiles are exactly the same, an update isn't required
        if (this.equals(newProfile))
            return;

        String logString = newProfile.getImei() + ": ";
        // Check all attributes, updating them whenever they differ
        if (!softwareVersion.equals(newProfile.getSoftwareVersion())) {
            ProfileDBManager.updateSoftwareVersion(this.imei, newProfile.getSoftwareVersion());
            logString = logString + "Old SW Version - " + softwareVersion + " | New SW Version - " + newProfile.getSoftwareVersion() + ".";
        }

        if (!simOperator.equals(newProfile.getSimOperator())) {
            ProfileDBManager.updateSimOperator(this.imei, newProfile.getSimOperator());
            logString = logString + "Old SIM Operator - " + simOperator + " | New SIM Operator - " + newProfile.getSimOperator() + ".";
        }

        if (!simOperatorName.equals(newProfile.getSimOperatorName())) {
            ProfileDBManager.updateSimOperatorName(this.imei, newProfile.getSimOperatorName());
            logString = logString + "Old SIM Operator Name - " + simOperatorName + " | New SIM Operator Name - " + newProfile.getSimOperatorName() + ".";
        }

        if(!simSerialNumber.equals(newProfile.getSimSerialNumber())) {
            ProfileDBManager.updateSimSerialNumber(this.imei, newProfile.getSimSerialNumber());
            logString = logString + "Old SIM Serial Number - " + simSerialNumber + " | New SIM Serial Number - " + newProfile.getSimSerialNumber() + ".";
        }

        if(!simCountryIso.equals(newProfile.getSimCountryIso())) {
            ProfileDBManager.updateSimCountryIso(this.imei, newProfile.getSimCountryIso());
            logString = logString + "Old SIM Country ISO - " + simCountryIso + " | New SIM Country ISO - " + newProfile.getSimCountryIso() + ".";
        }

        if(!imsiNumber.equals(newProfile.getImsiNumber())) {
            ProfileDBManager.updateImsiNumber(this.imei, newProfile.getImsiNumber());
            logString = logString + "Old IMSI Number - " + imsiNumber + " | New IMSI Number - " + newProfile.getImsiNumber() + ".";
        }

        if (!ipAddress.equals(newProfile.getIpAddress())) {
            ProfileDBManager.updateIpAddress(this.imei, newProfile.getIpAddress());
            logString = logString + "Old IP Address Location - " + ipAddress + " | New IP Address Location - " + newProfile.getIpAddress() + ".";
        }

        if(!osVersion.equals(newProfile.getOsVersion())) {
            ProfileDBManager.updateOsVersion(this.imei, newProfile.getOsVersion());
            logString = logString + "Old OS Version - " + osVersion + " | New OS Version - " + newProfile.getOsVersion() + ".";
        }

        if(!sdkVersion.equals(newProfile.getSdkVersion())) {
            ProfileDBManager.updateSdkVersion(this.imei, newProfile.getSdkVersion());
            logString = logString + "Old SDK Version - " + sdkVersion + " | New SDK Version - " + newProfile.getSdkVersion() + ".";
        }

        if(!deviceName.equals(newProfile.getDeviceName())) {
            ProfileDBManager.updateDeviceName(this.imei, newProfile.getDeviceName());
            logString = logString + "Old Device Name - " + deviceName + " | New Device Name - " + newProfile.getDeviceName() + ".";
        }

        if(!keyboardLanguage.equals(newProfile.getKeyboardLanguage())) {
            ProfileDBManager.updateKeyboardLanguage(this.imei, newProfile.getKeyboardLanguage());
            logString = logString + "Old Keyboard Language - " + keyboardLanguage + " | New Keyboard Language - " + newProfile.getKeyboardLanguage() + ".";
        }

        if(!networksSSID.equals(newProfile.getNetworksSSID())) {
            ProfileDBManager.updateNetworksSSID(this.imei, newProfile.getNetworksSSID());
            logString = logString + "Old Networks - " + networksSSID.toString() + " | New Networks - " + newProfile.getNetworksSSID().toString() + ".";
        }

        if(!googleAccounts.equals(newProfile.getGoogleAccounts())) {
            ProfileDBManager.updateGoogleAccounts(this.imei, newProfile.getGoogleAccounts());
            logString = logString + "Old Google Accounts - " + googleAccounts.toString() + " | New Google Accounts - " + newProfile.getGoogleAccounts().toString() + ".";
        }

        if(!memorizedAccounts.equals(newProfile.getMemorizedAccounts())) {
            ProfileDBManager.updateMemorizedAccounts(this.imei, newProfile.getMemorizedAccounts());
            logString = logString + "Old Accounts - " + memorizedAccounts.toString() + " | New Accounts - " + newProfile.getMemorizedAccounts().toString() + ".";
        }

        if(!inputMethods.equals(newProfile.getInputMethods())) {
            ProfileDBManager.updateInputMethods(this.imei, newProfile.getInputMethods());
            logString = logString + "Old Input Methods - " + inputMethods.toString() + " | New Input Methods - " + newProfile.getInputMethods().toString() + ".";
        }

        if(!installedApplications.equals(newProfile.getInstalledApplications())) {
            ProfileDBManager.updateInstalledApplications(this.imei, newProfile.getInstalledApplications());
            logString = logString + "Old Applications - " + installedApplications.toString() + " | New Applications - " + newProfile.getInstalledApplications().toString() + ".";
        }

        if (ServerUtils.SERVER_DEPLOY) LogManager.getInstance().logInfo(logString);
        System.out.println(logString);
    }
}