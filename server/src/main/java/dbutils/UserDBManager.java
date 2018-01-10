package dbutils;

import exceptions.UserDoesNotExistsException;
import models.User;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class UserDBManager extends DatabaseManager {

    /**
     * Insert a new user in the database
     * @param username the username of the new user
     * @throws Exception if the connection or the transaction fails
     */
    public static String insertUser(String username, PublicKey publicKey, SecretKey aesKey) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        PreparedStatement st = con.prepareStatement("INSERT INTO Users VALUES (?, ?, ?, ?, ?) ");
        st.setString(1, username);
        String pubk = new BASE64Encoder().encode(publicKey.getEncoded());
        st.setString(2, pubk);
        String aes = new BASE64Encoder().encode(aesKey.getEncoded());
        st.setString(3, aes);
        byte[] r = new byte[8];
        new Random().nextBytes(r);
        String nonce = Base64.encodeBase64String(r);
        st.setString(4, nonce);
        String code = String.format("%04d", new Random().nextInt(10000));
        st.setString(5, code);
        st.execute();
        st.close();
        endTransaction(con);
        con.close();
        return code;
    }

    /**
     * Updates the nonce for a specified user, generating a new random nonce
     * @param username the user to update the nonce
     * @return the new nonce
     * @throws UserDoesNotExistsException if the username doesn't match a stored user
     */
    public static String updateNonce(String username) throws Exception {
        Connection con = startConnection();
        if (hasUser(username)) {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("UPDATE Users SET NONCE = ? WHERE USERNAME = ?");
            byte[] r = new byte[8];
            new Random().nextBytes(r);
            String nonce = Base64.encodeBase64String(r);
            st.setString(1, nonce);
            st.setString(2, username);
            st.executeUpdate();
            st.close();
            endTransaction(con);
            con.close();
            return nonce;
        } else {
            con.close();
            throw new UserDoesNotExistsException();
        }
    }

    /**
     * Returns the current nonce of a given user
     * @param username the username of the user
     * @return the current nonce of the corresponding user
     * @throws UserDoesNotExistsException if the username doesn't match a registered user
     */
    public static String getCurrentNonce(String username) throws Exception {
        Connection con = startConnection();
        try {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("SELECT * FROM Users WHERE USERNAME = ?");
            st.setString(1, username);
            ResultSet query = st.executeQuery();
            query.next();
            String nonce = query.getString("NONCE");
            st.close();
            endTransaction(con);
            con.close();
            return nonce;
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw new UserDoesNotExistsException();
        }
    }

    /**
     * Updates the code for a specified user, generating a new random code
     * @param username the user to update the code
     * @return the new code
     * @throws UserDoesNotExistsException if the username doesn't match a stored user
     */
    public static String updateCode(String username) throws Exception {
        Connection con = startConnection();
        if (hasUser(username)) {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("UPDATE Users SET CODE = ? WHERE USERNAME = ?");
            String code = String.format("%04d", new Random().nextInt(10000));
            st.setString(1, code);
            st.setString(2, username);
            st.executeUpdate();
            st.close();
            endTransaction(con);
            con.close();
            return code;
        } else {
            con.close();
            throw new UserDoesNotExistsException();
        }
    }

    /**
     * Returns the current code of a given user
     * @param username the username of the user
     * @return the current code of the corresponding user
     * @throws UserDoesNotExistsException if the username doesn't match a registered user
     */
    public static String getCurrentCode(String username) throws Exception {
        Connection con = startConnection();
        try {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("SELECT * FROM Users WHERE USERNAME = ?");
            st.setString(1, username);
            ResultSet query = st.executeQuery();
            query.next();
            String nonce = query.getString("CODE");
            st.close();
            endTransaction(con);
            con.close();
            return nonce;
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw new UserDoesNotExistsException();
        }
    }

    /**
     * Returns the symmetric key of a given user
     * @param username the username of the user
     * @return the symmetric key of the corresponding user
     * @throws UserDoesNotExistsException if the username doesn't match a registered user
     */
    public static SecretKey getSymmetricKey(String username) throws Exception {
        Connection con = startConnection();
        try {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("SELECT * FROM Users WHERE USERNAME = ?");
            st.setString(1, username);
            ResultSet query = st.executeQuery();
            query.next();
            String aes = query.getString("AESKEY");
            st.close();
            endTransaction(con);
            con.close();
            return new SecretKeySpec(new BASE64Decoder().decodeBuffer(aes), "AES");
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw new UserDoesNotExistsException();
        }
    }

    /**
     * Retrieves a user given his username
     * @param username the username of the user to be retrieved
     * @return the User instance that corresponds to the given username
     * @throws UserDoesNotExistsException if the given username doesn't match a stored user
     */
    public static User getUser(String username) throws Exception {
        Connection con = startConnection();
        try {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("SELECT * FROM Users WHERE USERNAME = ?");
            st.setString(1, username);
            ResultSet query = st.executeQuery();
            query.next();

            String pubk = query.getString("PUBLICKEY");
            byte[] keyBytes = new BASE64Decoder().decodeBuffer(pubk);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            String aes = query.getString("AESKEY");
            SecretKey aeskey = new SecretKeySpec(new BASE64Decoder().decodeBuffer(aes), "AES");
            User u = new User(query.getString("USERNAME"), publicKey, aeskey);
            u.setNonce(query.getString("NONCE"));
            u.setCode(query.getString("CODE"));

            st.close();
            query.close();
            endTransaction(con);
            con.close();
            return u;
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw new UserDoesNotExistsException();
        }
    }

    /**
     * Returns true or false whether a given username has a matching User stored in the database
     * @param username the username of the user
     * @return true if a match is found, false otherwise
     */
    public static boolean hasUser(String username) throws Exception {
        Connection con = startConnection();
        beginTransaction(con);
        PreparedStatement st = con.prepareStatement("SELECT * FROM Users WHERE USERNAME = ?");
        st.setString(1, username);
        boolean ret = st.executeQuery().next();
        st.close();
        endTransaction(con);
        con.close();
        return ret;
    }

    /**
     * Removes a specific user from the system
     * @param username the username of the user
     */
    public static void removeUser(String username) throws Exception {
        Connection connection = startConnection();
        beginTransaction(connection);
        PreparedStatement query = connection.prepareStatement("DELETE FROM Users WHERE USERNAME = ?");
        query.setString(1, username);
        query.execute();
        query.close();
        endTransaction(connection);
        connection.close();
    }

    /**
     * Removes all stored users, this method is only used for unit tests
     */
    public static void removeAllUsers() throws Exception {
        Connection connection = startConnection();
        beginTransaction(connection);
        PreparedStatement query = connection.prepareStatement("DELETE FROM Users WHERE TRUE");
        query.execute();
        query.close();
        endTransaction(connection);
        connection.close();
    }
}
