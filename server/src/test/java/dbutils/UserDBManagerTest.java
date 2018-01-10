package dbutils;


import exceptions.UserDoesNotExistsException;
import models.User;
import org.junit.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

public class UserDBManagerTest extends DatabaseManager {

    private String username = "Name";
    private static PublicKey publicKey;
    private static SecretKey aesKey;

    @BeforeClass
    public static void initKey() throws Exception {
        publicKey = generatePublicKey();
        aesKey = generateAesKey();
    }

    @After
    public void destroy() throws Exception {
        UserDBManager.removeAllUsers();
    }

    @Test
    public void successInsertUser() throws Exception {
        String code = UserDBManager.insertUser(username, publicKey, aesKey);
        User u = UserDBManager.getUser(username);
        Assert.assertEquals(username, u.getUsername());
        Assert.assertEquals(publicKey, u.getPublicKey());
        Assert.assertEquals(aesKey, u.getAesKey());
        Assert.assertNotNull(u.getCode());
        Assert.assertNotNull(u.getNonce());
    }

    @Test
    public void updateCode() throws Exception {
        String code = UserDBManager.insertUser(username, publicKey, aesKey);
        // Update the nonce
        User u = UserDBManager.getUser(username);
        Assert.assertEquals(u.getCode(), code);
        String newcode = UserDBManager.updateCode(username);
        String newcodeget = UserDBManager.getCurrentCode(username);
        Assert.assertNotEquals(newcode, code);
        Assert.assertEquals(newcode, newcodeget);
    }

    @Test
    public void successHasUser() throws Exception {
        UserDBManager.insertUser(username + '1', publicKey, aesKey);

        Assert.assertTrue(UserDBManager.hasUser(username +'1'));
    }

    @Test(expected = UserDoesNotExistsException.class)
    public void getInexistentUser() throws Exception {
        UserDBManager.getUser("none");
    }
}
