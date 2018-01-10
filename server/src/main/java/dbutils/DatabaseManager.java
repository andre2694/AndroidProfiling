package dbutils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseManager {

    protected static String db_url = "jdbc:mysql://localhost:3306/android_profiling?autoReconnect=true&useSSL=false";
    protected static String db_user = "profiling";
    protected static String db_password = "android-profiling";
    protected static String auth_url = "jdbc:mysql://localhost:3306/auth_profiling?autoReconnect=true&useSSL=false";
    protected static String auth_user = "profiling";
    protected static String auth_password = "android-profiling";

    /**
     * Start the connection with the database
     * @return A connection instance
     * @throws Exception if the connection fails, the database doesn't exists, the username and password mismatch the stored one, among other issues
     */
    protected static Connection startConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        return DriverManager.getConnection(db_url, db_user, db_password);
    }

    /**
     * Start the connection with the authentication database
     * @return A connection instance
     * @throws Exception if the connection fails, the database doesn't exists, the username and password mismatch the stored one, among other issues
     */
    protected static Connection startAuthConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        return DriverManager.getConnection(auth_url, auth_user, auth_password);
    }

    /**
     * Begin a transaction in the active connection
     * @param con the active connection
     */
    protected static void beginTransaction(Connection con) throws Exception {
        con.setAutoCommit(false);
    }

    /**
     * Ends a transaction in the active connection
     * @param con the active connection
     */
    protected static void endTransaction(Connection con) throws Exception {
        con.commit();
        con.setAutoCommit(true);
    }

    /**
     * Test only method that generates a Public Key
     * @return RSA 2048bits public key instance
     */
    protected static PublicKey generatePublicKey() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair key = gen.generateKeyPair();
        return key.getPublic();
    }

    /**
     * Test only method that generates an AES Key
     */
    public static SecretKey generateAesKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }
}
