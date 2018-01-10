package profiling.androidprofiling.utils;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

public class KeyStoreManager {

    public static PublicKey publicKey;
    public static PrivateKey privateKey;
    private static KeyStoreManager instance;
    final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    final String RSA_KEY_ALIAS = "RSA_PROFILING";
    final int RSA_BIT_LENGTH = 2048;
    final int AES_BIT_LENGTH = 256;
    final String RSA_CIPHER = KeyProperties.KEY_ALGORITHM_RSA + "/" + KeyProperties.BLOCK_MODE_ECB + "/" + KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1;
    final String SSL_PROVIDER = "AndroidOpenSSL";
    KeyStore keyStore;

    private KeyStoreManager() {
    }

    public static KeyStoreManager getInstance() {
        if (instance == null) {
            instance = new KeyStoreManager();
        }
        return instance;
    }

    public void initRSAKeys(Context context) throws Exception {
        loadKeyStore();
        try {
            loadRSAKeys();
        } catch (Exception e) {
            generateRSAKeys(context);
            loadRSAKeys();
        }
    }

    public PublicKey getPublicKey(Context context) {
        try {
            initRSAKeys(context);
            return publicKey;
        } catch (Exception e) {
            return null;
        }
    }

    private void loadKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);
    }

    private void generateRSAKeys(Context context) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException {
        if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 25);

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER);

            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(RSA_KEY_ALIAS)
                    .setKeySize(RSA_BIT_LENGTH)
                    .setKeyType(KeyProperties.KEY_ALGORITHM_RSA)
                    .setEndDate(end.getTime())
                    .setStartDate(start.getTime())
                    .setSerialNumber(BigInteger.ONE)
                    .setSubject(new X500Principal("CN = Secured Preference Store, O = Devliving Online"))
                    .build();

            keyGen.initialize(spec);
            keyGen.generateKeyPair();
        }
    }

    private void loadRSAKeys() throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException {
        if (keyStore.containsAlias(RSA_KEY_ALIAS) && keyStore.entryInstanceOf(RSA_KEY_ALIAS, KeyStore.PrivateKeyEntry.class)) {
            KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_KEY_ALIAS, null);
            publicKey = entry.getCertificate().getPublicKey();
            privateKey = entry.getPrivateKey();
        } else
            throw new KeyStoreException();
    }

    public void initAESKey(Context context) {
        try {
            byte[] encoded = generateAESKey();
            PersistenceManager.getInstance().writeKey(encoded, context);
        } catch (Exception e) {
            Log.e("AES Key", e.toString());
        }
    }

    public SecretKey getAESKey(Context context) {
        try {
            byte[] encrypted = PersistenceManager.getInstance().loadKey(context);
            return new SecretKeySpec(encrypted, "AES");
        } catch (Exception e) {
            initAESKey(context);
            return getAESKey(context);
        }
    }

    byte[] generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");

        keyGen.init(AES_BIT_LENGTH);
        SecretKey sKey = keyGen.generateKey();
        return sKey.getEncoded();
    }

    byte[] RSAEncrypt(byte[] bytes) throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance(RSA_CIPHER, SSL_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
        cipherOutputStream.write(bytes);
        cipherOutputStream.close();

        return outputStream.toByteArray();
    }

    byte[] RSADecrypt(byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance(RSA_CIPHER, SSL_PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(bytes), cipher);

        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte) nextByte);
        }

        byte[] dbytes = new byte[values.size()];
        for (int i = 0; i < dbytes.length; i++) {
            dbytes[i] = values.get(i).byteValue();
        }

        cipherInputStream.close();
        return dbytes;
    }
}
