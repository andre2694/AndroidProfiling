package profiling.androidprofiling.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
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

import javax.crypto.SecretKey;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import profiling.androidprofiling.R;
import profiling.androidprofiling.services.BootstrapService;
import profiling.androidprofiling.services.InformationCollectorService;
import profiling.androidprofiling.services.NewProfileService;
import profiling.androidprofiling.utils.KeyStoreManager;
import profiling.androidprofiling.utils.PersistenceManager;
import profiling.androidprofiling.utils.ProfilingUtils;

import static profiling.androidprofiling.utils.ProfilingUtils.hmac;

public class InformationCollectionActivity extends AppCompatActivity {

    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_collection);
        PersistenceManager.getInstance().hideProgress();

        info = (TextView) findViewById(R.id.infoView);
        info.setText(PersistenceManager.getInstance().getChangedInformation());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(getBaseContext(), InformationCollectorService.class));
        stopService(new Intent(getBaseContext(), NewProfileService.class));
        stopService(new Intent(getBaseContext(), BootstrapService.class));
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(getBaseContext(), InformationCollectorService.class));
        stopService(new Intent(getBaseContext(), NewProfileService.class));
        stopService(new Intent(getBaseContext(), BootstrapService.class));
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), InformationCollectorService.class));
        stopService(new Intent(getBaseContext(), NewProfileService.class));
        stopService(new Intent(getBaseContext(), BootstrapService.class));
        this.finish();
    }

    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), InformationCollectorService.class));
        stopService(new Intent(getBaseContext(), NewProfileService.class));
        stopService(new Intent(getBaseContext(), BootstrapService.class));
        this.finish();
    }

    public void numberRequest(View view) {
        // Replace with your own IP to test...
        String url = getResources().getString(R.string.server_ip) + "/newnumber";
        // Post params to be sent to the server
        final HashMap<String, String> params = new HashMap<String, String>();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String androidIMEI = telephonyManager.getDeviceId();
        String simCountryIso = telephonyManager.getSimCountryIso();
        String simOperator = telephonyManager.getSimOperator();
        String simOperatorName = telephonyManager.getSimOperatorName();
        String simSerialNumber = telephonyManager.getSimSerialNumber();
        String imsiNumber = telephonyManager.getSubscriberId();

        SecretKey key = KeyStoreManager.getInstance().getAESKey(this);
        try {
            params.put("username", PersistenceManager.getInstance().getUsername());
            params.put("nonce", PersistenceManager.getInstance().getNonce());
            params.put("imei", hmac(key, androidIMEI));
            params.put("sim_country_iso", hmac(key, simCountryIso));
            params.put("sim_operator", hmac(key, simOperator));
            params.put("sim_operator_name", hmac(key, simOperatorName));
            params.put("sim_serial_number", hmac(key, simSerialNumber));
            params.put("imsi_number", hmac(key, imsiNumber));
        } catch (Exception e) {
            Log.e("Exception: ", e.toString());
        }

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.v("Response:%n %s", response.toString());
                        try {
                            JSONObject jo = new JSONObject(response.getString("message"));
                            PersistenceManager.getInstance().setCode(jo.getString("code"));
                            Intent i = new Intent(getApplicationContext(), ConfirmCodeActivity.class);
                            startActivity(i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
