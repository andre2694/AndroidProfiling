package profiling.androidprofiling.services;

import android.content.Intent;
import android.util.Log;
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
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import profiling.androidprofiling.R;
import profiling.androidprofiling.activities.ConfirmProfileCodeActivity;
import profiling.androidprofiling.activities.InformationCollectionActivity;
import profiling.androidprofiling.utils.PersistenceManager;
import profiling.androidprofiling.utils.ProfilingUtils;

public class NewProfileService extends ProfilingService {
    int mStartMode;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getProfile();
        String username = PersistenceManager.getInstance().getUsername();
        sendProfile(username);
        return mStartMode;
    }

    private void sendProfile(String username) {
        // Replace with your own IP to test...
        String URL = getResources().getString(R.string.server_ip) + "/update";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("profile", profile.toString());

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response: ", response.toString());
                        VolleyLog.v("Response:%n %s", response);
                        try {
                            JSONObject jo = new JSONObject(response.getString("message"));
                            String nonce = jo.getString("nonce");
                            PersistenceManager.getInstance().setNonce(nonce);
                            PersistenceManager.getInstance().setChangedInformation("New profile acquired.\n");
                            Intent i = new Intent(getApplicationContext(), InformationCollectionActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ConfirmProfileCodeActivity.activity.finish();
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "New profile created.", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                try {
                    Toast.makeText(getApplicationContext(), (String) new JSONObject(new String(error.networkResponse.data)).get("message"), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(),"", Toast.LENGTH_SHORT).show();
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
