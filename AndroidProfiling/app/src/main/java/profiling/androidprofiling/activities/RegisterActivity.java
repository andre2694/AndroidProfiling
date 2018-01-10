package profiling.androidprofiling.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import profiling.androidprofiling.services.BootstrapService;
import profiling.androidprofiling.utils.PersistenceManager;
import profiling.androidprofiling.utils.ProfilingUtils;

public class RegisterActivity extends AppCompatActivity {
    static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    EditText username;
    EditText email;
    EditText pass;
    EditText confPass;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button login = (Button) findViewById(R.id.register_login_button);
        Button register = (Button) findViewById(R.id.register_button);

        username = (EditText) findViewById(R.id.register_name);
        email = (EditText) findViewById(R.id.register_email);
        pass = (EditText) findViewById(R.id.register_password);
        confPass = (EditText) findViewById(R.id.register_confirm_password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ProfilingUtils.checkInternet(getApplicationContext())) {
                    if (validateRegisterInputs()) {
                        makeRequest();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Internet not available!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void makeRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.GET_ACCOUNTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
            else {
                registerRequest();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean canReadPhone = false;
        boolean canReadContacts = false;
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canReadPhone = true;
                    if (canReadContacts)
                        registerRequest();
                    else
                        makeRequest();
                }
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canReadContacts = true;
                    if (canReadPhone)
                        registerRequest();
                    else
                        makeRequest();
                }
            }
        }
    }

    public void registerRequest() {
        PersistenceManager.getInstance().showProgress(this);
        // Replace with your own IP to test...
        String URL = getResources().getString(R.string.auth_ip) + "/register";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", ((EditText) (findViewById(R.id.register_name))).getText().toString());
        params.put("email", ((EditText) (findViewById(R.id.register_email))).getText().toString());
        try {
            params.put("password", ProfilingUtils.hash(((EditText) (findViewById(R.id.register_password))).getText().toString()));
        } catch (Exception e) {
        }
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response: ", response.toString());
                        VolleyLog.v("Response:%n %s", response);
                        PersistenceManager.getInstance().setUsername(username.getText().toString());
                        PersistenceManager.getInstance().setEmail(email.getText().toString());
                        startService(new Intent(getBaseContext(), BootstrapService.class));
//                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (PersistenceManager.getInstance().isProgressDisp())
                        PersistenceManager.getInstance().hideProgress();
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

    public boolean validateRegisterInputs() {
        boolean resp = true;
        if (username.getText().toString().trim().equalsIgnoreCase("")) {
            username.setError("This field can not be blank.");
            resp = false;
        }
        if (email.getText().toString().trim().equalsIgnoreCase("")) {
            email.setError("This field can not be blank.");
            resp = false;
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                email.setError("Invalid Email!");
                resp = false;
            }
        }
        if (pass.getText().toString().trim().equalsIgnoreCase("")) {
            pass.setError("This field can not be blank.");
            resp = false;
        }
        if (confPass.getText().toString().trim().equalsIgnoreCase("")) {
            confPass.setError("This field can not be blank.");
            resp = false;
        } else {
            if (!confPass.getText().toString().equals(pass.getText().toString())) {
                confPass.setError("Passwords do not match.");
                resp = false;
            }
        }
        return resp;
    }
}
