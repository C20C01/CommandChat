package io.github.c20c01.commandchat.util.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

public class AESTool extends Encryption {
    private final Cipher ENCRYPT_CIPHER;
    private final Cipher DECRYPT_CIPHER;

    public AESTool(SecretKey secretKey) {
        try {
            ENCRYPT_CIPHER = Cipher.getInstance("AES");
            ENCRYPT_CIPHER.init(Cipher.ENCRYPT_MODE, secretKey);
            DECRYPT_CIPHER = Cipher.getInstance("AES");
            DECRYPT_CIPHER.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey generateSecretKey() {
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            keygenerator.init(256);
            return keygenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey getSecretKey(byte[] key) {
        return new SecretKeySpec(key, "AES");
    }

    public byte[] encrypt(byte[] unencrypted) {
        return handle(unencrypted, ENCRYPT_CIPHER);
    }

    public byte[] decrypt(byte[] encrypted) {
        return handle(encrypted, DECRYPT_CIPHER);
    }
}
