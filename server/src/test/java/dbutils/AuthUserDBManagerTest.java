package dbutils;

import exceptions.UserDoesNotExistsException;
import models.AuthUser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


public class AuthUserDBManagerTest extends DatabaseManager {

    private String username = "Name";
    private String password = "pw";
    private String email = "email@email.com";

    @After
    public void destroy() throws Exception {
        AuthUserDBManager.removeAllUsers();
    }

    @Test
    public void successInsertUser() throws Exception {
        AuthUserDBManager.insertUser(username, password, email);

        AuthUser u = AuthUserDBManager.getUser(username);
        Assert.assertEquals(username, u.getUsername());
        Assert.assertEquals(password, u.getPassword());
        Assert.assertEquals(email, u.getEmail());
    }

    @Test
    public void successHasUser() throws Exception {
        AuthUserDBManager.insertUser(username + '1', password, email);

        Assert.assertTrue(AuthUserDBManager.hasUser(username +'1'));
    }

    @Test
    public void successUpdatePassword() throws Exception {
        String us = username + "2";
        AuthUserDBManager.insertUser(us, password, email);

        Assert.assertEquals(AuthUserDBManager.getUserPassword(us), password);

        AuthUserDBManager.updatePassword(us, "newpass");

        Assert.assertEquals(AuthUserDBManager.getUserPassword(us), "newpass");
    }

    @Test(expected = UserDoesNotExistsException.class)
    public void getInexistentUser() throws Exception {
        AuthUserDBManager.getUser("none");
    }

    @Test(expected = UserDoesNotExistsException.class)
    public void getInexistentUserPassword() throws Exception {
        AuthUserDBManager.getUserPassword("none");
    }

    @Test(expected = UserDoesNotExistsException.class)
    public void updateInexistentUserPassword() throws Exception {
        AuthUserDBManager.updatePassword("none", password);
    }
}
