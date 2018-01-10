package models;

import javax.crypto.SecretKey;
import java.security.PublicKey;

public class User {

    private String username;
    private PublicKey publicKey;
    private SecretKey aesKey;
    private String nonce;
    private String code;

    public User(String username, PublicKey publicKey, SecretKey aesKey) {
        this.username = username;
        this.publicKey = publicKey;
        this.aesKey = aesKey;
    }

    public String getUsername() {
        return username;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public SecretKey getAesKey() {
        return this.aesKey;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
