package profiling.androidprofiling.services;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.PublicKey;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import profiling.androidprofiling.R;
import profiling.androidprofiling.activities.ConfirmCodeActivity;
import profiling.androidprofiling.activities.RegisterActivity;
import profiling.androidprofiling.utils.KeyStoreManager;
import profiling.androidprofiling.utils.PersistenceManager;
import profiling.androidprofiling.utils.ProfilingUtils;

public class BootstrapService extends ProfilingService {
    int mStartMode;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            KeyStoreManager.getInstance().initRSAKeys(this);
            KeyStoreManager.getInstance().initAESKey(this);
        } catch (Exception e) {
            Log.e("Exception: ", e.toString());
        }
        getProfile();
        String username = PersistenceManager.getInstance().getUsername();
        sendProfile(username);
        return mStartMode;
    }

    private void sendProfile(String username) {
        // Replace with your own IP to test...
        String URL = getResources().getString(R.string.server_ip) + "/bootstrap";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("profile", profile.toString());
        PublicKey publicKey = KeyStoreManager.getInstance().getPublicKey(this);
        String pubk = Base64.encodeToString(publicKey.getEncoded(), 16 | Base64.NO_CLOSE);
        params.put("public_key", pubk);
        SecretKey aesKey = KeyStoreManager.getInstance().getAESKey(this);
        String aes = Base64.encodeToString(aesKey.getEncoded(), 16 | Base64.NO_CLOSE);
        params.put("symmetric_key", aes);
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Bootstrap Response: ", response.toString());
                        VolleyLog.v("Response:%n %s", response);
                        try {
                            JSONObject jo = new JSONObject(response.getString("message"));
                            PersistenceManager.getInstance().setCode(jo.getString("code"));
                            PersistenceManager.getInstance().setChangedInformation("New profile created.\n");
                            Intent i = new Intent(getApplicationContext(), ConfirmCodeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            RegisterActivity.activity.finish();
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (PersistenceManager.getInstance().isProgressDisp())
                        PersistenceManager.getInstance().hideProgress();
                    Toast.makeText(getApplicationContext(), (String) new JSONObject(new String(error.networkResponse.data)).get("message"), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                }
                Log.e("Volley Error: ", error.toString());
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        // add the request object to the queue to be executed
        SSLContext context = ProfilingUtils.getSSLContext();
        final SSLSocketFactory sslFactory = context.getSocketFactory();
        final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv =
                        HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify(getResources().getString(R.string.hostname), session);
            }
        };
        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(java.net.URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(sslFactory);
                    httpsURLConnection.setHostnameVerifier(hostnameVerifier);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
        Volley.newRequestQueue(this, hurlStack).add(req);
    }
}
