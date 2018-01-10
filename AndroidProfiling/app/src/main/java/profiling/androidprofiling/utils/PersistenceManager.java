package profiling.androidprofiling.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class PersistenceManager {
    private static PersistenceManager instance;
    private String username;
    private String email;
    private String nonce;
    private ProgressDialog progress;
    private boolean progressDisp = false;
    private String changedInformation;
    private String code;

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String e) {
        this.email = e;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public String getNonce() {
        return this.nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public boolean isProgressDisp() {
        return progressDisp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getChangedInformation() {
        return changedInformation;
    }

    public void setChangedInformation(String changedInformation) {
        this.changedInformation = changedInformation;
    }

    public void writeKey(byte[] data, Context context) {
        SharedPreferences settings = context.getSharedPreferences("KEY_PREFERENCES", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("aeskey", Base64.encodeToString(data, Base64.DEFAULT));
        editor.commit();
    }

    public byte[] loadKey(Context context) {
        SharedPreferences settings = context.getSharedPreferences("KEY_PREFERENCES", 0);
        return Base64.decode(settings.getString("aeskey", null), Base64.DEFAULT);
    }

    public void showProgress(Context context) {
        progress = new ProgressDialog(context);
        progress.setMessage("Loading...");
        progress.show();
        progressDisp = true;
    }

    public void hideProgress() {
        progress.dismiss();
        progressDisp = false;
    }
}
