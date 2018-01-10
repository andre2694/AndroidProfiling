package dbutils;

import exceptions.UserDoesNotExistsException;
import models.AuthUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthUserDBManager extends DatabaseManager {

    /**
     * Insert a new user in the database
     * @param username the username of the new user
     * @throws Exception if the connection or the transaction fails
     */
    public static void insertUser(String username, String password, String email) throws Exception {
        Connection con = startAuthConnection();
        beginTransaction(con);
        PreparedStatement st = con.prepareStatement("INSERT INTO Users VALUES (?, ?, ?) ");
        st.setString(1, username);
        st.setString(2, password);
        st.setString(3, email);
        st.execute();
        st.close();
        endTransaction(con);
        con.close();
    }

    /**
     * Returns the hash of the password of a user given his username
     * @param username the username of the user
     * @return the hash of the password
     * @throws UserDoesNotExistsException if the username doesn't match a stored user
     */
    public static String getUserPassword(String username) throws Exception {
        Connection con = startAuthConnection();
        try {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("SELECT * FROM Users WHERE USERNAME = ?");
            st.setString(1, username);
            ResultSet query = st.executeQuery();
            query.next();
            String password = query.getString("PASSWORDHASH");
            st.close();
            endTransaction(con);
            con.close();
            return password;
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw new UserDoesNotExistsException();
        }
    }

    /**
     * Updates the password of a given user
     * @param username the username of the user
     * @param newpass the new password hash
     * @throws UserDoesNotExistsException if the username doesn't match a stored user
     */
    public static void updatePassword(String username, String newpass) throws Exception {
        Connection con = startAuthConnection();
        if (hasUser(username)) {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("UPDATE Users SET PASSWORDHASH = ? WHERE USERNAME = ?");
            st.setString(1, newpass);
            st.setString(2, username);
            st.executeUpdate();
            st.close();
            endTransaction(con);
            con.close();
        } else {
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
    public static AuthUser getUser(String username) throws Exception {
        Connection con = startAuthConnection();
        try {
            beginTransaction(con);
            PreparedStatement st = con.prepareStatement("SELECT * FROM Users WHERE USERNAME = ?");
            st.setString(1, username);
            ResultSet query = st.executeQuery();
            query.next();
            AuthUser u = new AuthUser(query.getString("USERNAME"), query.getString("PASSWORDHASH"), query.getString("EMAIL"));
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
        Connection con = startAuthConnection();
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
        Connection connection = startAuthConnection();
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
        Connection connection = startAuthConnection();
        beginTransaction(connection);
        PreparedStatement query = connection.prepareStatement("DELETE FROM Users WHERE TRUE");
        query.execute();
        query.close();
        endTransaction(connection);
        connection.close();
    }
}
