package profiling.androidprofiling.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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
import profiling.androidprofiling.utils.PersistenceManager;
import profiling.androidprofiling.utils.ProfilingUtils;

public class CancelAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel);

        Button confirm = (Button) findViewById(R.id.confirm);
        Button exit = (Button) findViewById(R.id.exit_cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAccount(v);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void cancelAccount(View view) {
        // Replace with your own IP to test...
        String URL = getResources().getString(R.string.auth_ip) + "/cancel";
        // Post params to be sent to the server
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", ((EditText) (findViewById(R.id.cancel_username))).getText().toString());
        try {
            params.put("password", ProfilingUtils.hash(((EditText) (findViewById(R.id.cancel_password))).getText().toString()));
            params.put("nonce", PersistenceManager.getInstance().getNonce());
        } catch (Exception e) {
        }

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.v("Response:%n %s", response.toString());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                        Toast.makeText(getApplicationContext(), "Account cancelled.", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(getApplicationContext(), (String) new JSONObject(new String(error.networkResponse.data)).get("message"), Toast.LENGTH_SHORT).show();
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