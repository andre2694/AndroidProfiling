package profiling.androidprofiling.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class ProfilingUtils {
    private static final String pem = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDrzCCApegAwIBAgIEVXe7ETANBgkqhkiG9w0BAQsFADCBhzELMAkGA1UEBhMC\n" +
            "UFQxDzANBgNVBAgTBkxpc2JvbjEPMA0GA1UEBxMGTGlzYm9uMQwwCgYDVQQKEwNJ\n" +
            "U1QxDDAKBgNVBAsTA0lTVDE6MDgGA1UEAxMxZWMyLTM0LTI0OS0zMi0xODguZXUt\n" +
            "d2VzdC0xLmNvbXB1dGUuYW1hem9uYXdzLmNvbTAeFw0xNzA0MjIxNjIzNTJaFw0x\n" +
            "ODA0MTcxNjIzNTJaMIGHMQswCQYDVQQGEwJQVDEPMA0GA1UECBMGTGlzYm9uMQ8w\n" +
            "DQYDVQQHEwZMaXNib24xDDAKBgNVBAoTA0lTVDEMMAoGA1UECxMDSVNUMTowOAYD\n" +
            "VQQDEzFlYzItMzQtMjQ5LTMyLTE4OC5ldS13ZXN0LTEuY29tcHV0ZS5hbWF6b25h\n" +
            "d3MuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6zzLXNRmhZX3\n" +
            "bq/1OWMHk+xkIa3P1coVJvDclSGlk+xyoPKiOnObus5LixvbL5bYYq6PP3RHTLBX\n" +
            "wfRfGyojpgB4IB28x24Z8VKB7T6Q+zRZ2A+KD1yI878IZhcgn0z3HfaMju6Q93ww\n" +
            "a0Xk63lESzeM/Q0lQ8WdqlFQBkXng+R2Np9RZ7XC9cnXtonoo6X8qLsMosnu/sMb\n" +
            "gEWN4qLESx/UGCRliAvD8uYPIJ9GwjunI5PVKXwiCj5n0aY8Rccg87O3a4zqSkDw\n" +
            "5q7jQisQWwaeQBw58eJwhx2wx+TJ9wQptHfBbpDkxE4RwDEw0/wpgUhQ8O8EtvlR\n" +
            "Iq1YPiK/dQIDAQABoyEwHzAdBgNVHQ4EFgQUHy/+8jBrk8Rqnb+q0pE8/o9aJtww\n" +
            "DQYJKoZIhvcNAQELBQADggEBAF8aW0iMtI2YxS/G6aJ/px8L7TUFxLllQAHoVKpK\n" +
            "4Q8dHJGGAkosTU+ffB/lZdCyTJd8PiH0or5Jkgf4l/5+ps85aJNE4dbyLO2Xzydi\n" +
            "OhW5VsnH5CgT+RGNZJZpUyH+kN2GtaBOsnzTDFf6I96AkldAd13Vn5E599aQY0YB\n" +
            "DSjiIqIJI3HUOXB/y89CodilyOEShO29Kd7ftaAHJwGgcTSwOQXIDJk0R7qs86N0\n" +
            "32u+h2tz6T0pgXfWBuleroULdzRq4WiDXjmRg+jGMva14loLcW8zHvqosSnDQY++\n" +
            "lDjdV5UYHCQ2XRLzHXyHTPoNOdHXlYi2+7G2mY2RoZq95aM=\n" +
            "-----END CERTIFICATE-----\n";

    public static boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static String hmac(SecretKey key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        sha256_HMAC.init(key);
        return Base64.encodeToString(sha256_HMAC.doFinal(data.getBytes("UTF-8")), Base64.NO_WRAP | Base64.NO_CLOSE);
    }

    public static String hash(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes());
        byte[] digest = md.digest();
        return Base64.encodeToString(digest, Base64.NO_WRAP | Base64.NO_CLOSE);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer))
            return capitalize(model);
        else
            return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0)
            return "";
        char first = s.charAt(0);
        if (Character.isUpperCase(first))
            return s;
        else
            return Character.toUpperCase(first) + s.substring(1);
    }

    public static String getApplicationLabel(ApplicationInfo info, PackageManager pm) {
        return (String) pm.getApplicationLabel(info);
    }

    public static X509Certificate convertToX509Cert(String certificateString) throws CertificateException {
        X509Certificate certificate = null;
        CertificateFactory cf = null;
        try {
            if (certificateString != null && !certificateString.trim().isEmpty()) {
                certificateString = certificateString.replace("-----BEGIN CERTIFICATE-----\n", "")
                        .replace("-----END CERTIFICATE-----", ""); // NEED FOR PEM FORMAT CERT STRING
                byte[] certificateData = Base64.decode(certificateString, Base64.DEFAULT);
                cf = CertificateFactory.getInstance("X509");
                certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificateData));
            }
        } catch (CertificateException e) {
            throw new CertificateException(e);
        }
        return certificate;
    }

    public static SSLContext getSSLContext() {
        try {
            Certificate ca = convertToX509Cert(pem);
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            return context;
        } catch (Exception e) {
            return null;
        }
    }
}
