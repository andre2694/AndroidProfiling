package profiling.androidprofiling.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.androidfung.geoip.api.ApiManager;
import com.androidfung.geoip.model.GeoIpResponseModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.crypto.SecretKey;

import profiling.androidprofiling.utils.KeyStoreManager;
import profiling.androidprofiling.utils.PersistenceManager;

import static profiling.androidprofiling.utils.ProfilingUtils.getApplicationLabel;
import static profiling.androidprofiling.utils.ProfilingUtils.getDeviceName;
import static profiling.androidprofiling.utils.ProfilingUtils.hmac;

public abstract class ProfilingService extends Service {
    protected JSONObject profile;
    IBinder mBinder;
    boolean mAllowRebind;

    protected void sendProfile() {
    }

    private void sharedResponse(String response) {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putString("country", response);
        editor.commit();
    }

    public String getLocation() {
        final ApiManager apiManager = new ApiManager(Volley.newRequestQueue(this));
        apiManager.getGeoIpInfo(new Response.Listener<GeoIpResponseModel>() {
            @Override
            public void onResponse(GeoIpResponseModel response) {
                sharedResponse(response.getCountry());
                return;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        return m.getString("country", "");
    }

    protected void getProfile() {
        // Retrieve IMEI, Software Version, SIM information and IMSI number, Network Operator information is also possible to retrieve
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String androidIMEI = telephonyManager.getDeviceId();
        if (androidIMEI == null)
            androidIMEI = "imei-" + PersistenceManager.getInstance().getUsername();

        String androidSoftwareVersion = telephonyManager.getDeviceSoftwareVersion();
        if (androidSoftwareVersion == null)
            androidSoftwareVersion = "null";

        String simCountryIso = telephonyManager.getSimCountryIso();
        if (simCountryIso == null)
            simCountryIso = "null";

        String simOperator = telephonyManager.getSimOperator();
        if (simOperator == null)
            simOperator = "null";

        String simOperatorName = telephonyManager.getSimOperatorName();
        if (simOperatorName == null)
            simOperatorName = "null";

        String simSerialNumber = telephonyManager.getSimSerialNumber();
        if (simSerialNumber == null)
            simSerialNumber = "null";

        String imsiNumber = telephonyManager.getSubscriberId();

        // Retrieve MAC and IP Addresses and Memorized Networks
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!manager.isWifiEnabled())
            manager.setWifiEnabled(true);

        WifiInfo info = manager.getConnectionInfo();
        String macAddress = info.getMacAddress();
        if (macAddress == null)
            macAddress = "null";

        String ip = getLocation();
        if (ip == null)
            ip = Formatter.formatIpAddress(info.getIpAddress());

        List<WifiConfiguration> configs = manager.getConfiguredNetworks();
        ArrayList<String> memorizedNetworksSSID = new ArrayList<>();
        if (configs != null)
            for (WifiConfiguration config : configs)
                memorizedNetworksSSID.add(config.SSID);

        // Operating System and SDK Version
        String osVersion = android.os.Build.VERSION.RELEASE;
        if (osVersion == null)
            osVersion = "null";

        int sdkVersion = android.os.Build.VERSION.SDK_INT;

        // Memorized Accounts
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accountList = accountManager.getAccounts();
        List<Account> googleAccounts = new ArrayList<>();
        List<Account> otherAccounts = new ArrayList<>();
        if (accountList != null) {
            for (Account a : accountList) {
                if (a.type.equals("com.google"))
                    googleAccounts.add(a);
                else
                    otherAccounts.add(a);
            }
        }

        // Screen Resolution
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        String screen_resolution = width + "," + height;

        // Retrieve Idiom and Keyboard information
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        InputMethodSubtype inputMethodSubtype = inputMethodManager.getCurrentInputMethodSubtype();
        String localeString = inputMethodSubtype.getLocale();
        String currentLanguage = "null";
        if (localeString == null) {
            localeString = "null";
            Locale locale = new Locale(localeString);
            if (locale.getDisplayLanguage() != null)
                currentLanguage = locale.getDisplayLanguage();
        }

        ArrayList<String> inputMethods = new ArrayList<>();
        if (inputMethodManager.getEnabledInputMethodList() != null) {
            for (InputMethodInfo inputMethodInfo : inputMethodManager.getEnabledInputMethodList()) {
                inputMethods.add(inputMethodInfo.getId());
            }
        }

        // Retrieved Installed Applications
        PackageManager packageManager = getPackageManager();
        ArrayList<String> installedApplications = new ArrayList<>();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(0);
        if (installedApps != null) {
            for (ApplicationInfo app : installedApps) {
                if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
                    installedApplications.add(getApplicationLabel(app, packageManager));
                else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                } else
                    installedApplications.add(getApplicationLabel(app, packageManager));
            }
        }

        // Create a JSON Object with the profiling info
        this.profile = new JSONObject();
        JSONArray networks = new JSONArray();
        JSONArray google_accounts = new JSONArray();
        JSONArray accounts = new JSONArray();
        JSONArray keyboards = new JSONArray();
        JSONArray applications = new JSONArray();
        SecretKey key = KeyStoreManager.getInstance().getAESKey(this);
        try {
            // IMEI and Software Version
            profile.put("imei", hmac(key, androidIMEI));
            profile.put("software_version", hmac(key, androidSoftwareVersion));
            // IMSI and SIM info
            profile.put("sim_country_iso", hmac(key, simCountryIso));
            profile.put("sim_operator", hmac(key, simOperator));
            profile.put("sim_operator_name", hmac(key, simOperatorName));
            profile.put("sim_serial_number", hmac(key, simSerialNumber));
            profile.put("imsi_number", hmac(key, imsiNumber));
            // MAC and IP Address and Memorized Networks
            profile.put("mac_address", hmac(key, macAddress));
            profile.put("ip_address", hmac(key, ip));
            for (String network : memorizedNetworksSSID)
                networks.put(hmac(key, network));
            profile.put("memorized_networks", networks);
            // Operating System Info
            profile.put("os_version", hmac(key, osVersion));
            profile.put("sdk_version", hmac(key, "" + sdkVersion));
            // Memorized Accounts
            for (Account account : googleAccounts) {
                google_accounts.put(hmac(key, account.name));
            }
            for (Account account : otherAccounts) {
                accounts.put(hmac(key, account.name));
            }
            profile.put("google_accounts", google_accounts);
            profile.put("memorized_accounts", accounts);
            // Device Name
            profile.put("device_name", hmac(key, getDeviceName()));
            // Screen Resolution
            profile.put("screen_resolution", hmac(key, screen_resolution));
            // Idiom and Keyboard
            profile.put("keyboard_language", hmac(key, currentLanguage));
            for (String keyboard : inputMethods)
                keyboards.put(hmac(key, keyboard));
            profile.put("keyboards", keyboards);
            // Installed Applications
            for (String app : installedApplications)
                applications.put(hmac(key, app));
            profile.put("installed_applications", applications);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }
}
