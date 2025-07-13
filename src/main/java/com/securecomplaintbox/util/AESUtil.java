package com.securecomplaintbox.util;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

public class AESUtil {
    private static final String ALGORITHM = "AES";

    public static String encrypt(String plainText) throws GeneralSecurityException {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plain text cannot be null or empty");
        }
        
        try {
            String secret = ConfigUtil.getEncryptionSecret();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] enc = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(enc);
        } catch (Exception e) {
            throw new GeneralSecurityException("Encryption failed: " + e.getMessage(), e);
        }
    }

    public static String decrypt(String cipherText) throws GeneralSecurityException {
        if (cipherText == null || cipherText.isEmpty()) {
            throw new IllegalArgumentException("Cipher text cannot be null or empty");
        }
        
        try {
            String secret = ConfigUtil.getEncryptionSecret();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] dec = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(dec, "UTF-8");
        } catch (Exception e) {
            throw new GeneralSecurityException("Decryption failed: " + e.getMessage(), e);
        }
    }
}
