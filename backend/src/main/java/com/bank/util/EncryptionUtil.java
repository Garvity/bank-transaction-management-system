package com.bank.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY = "GarvityBankSystemSuperSecureKe32"; // 32 bytes for AES-256
    private static final String INIT_VECTOR = "GarvityBankIV16B"; // 16 bytes for CBC IV

    public static String encrypt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException("Encryption failed: " + ex.getMessage(), ex);
        }
    }

    public static String decrypt(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.trim().isEmpty()) {
            return encryptedValue;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("Decryption failed: " + ex.getMessage(), ex);
        }
    }
}
