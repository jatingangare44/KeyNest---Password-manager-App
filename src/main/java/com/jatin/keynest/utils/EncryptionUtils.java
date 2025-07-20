package com.jatin.keynest.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES";

    private static SecretKeySpec getKey(String inputKey) {
        // Normalize to 16 bytes using UTF-8
        byte[] keyBytes = (inputKey + "0000000000000000")
                .substring(0, 16)
                .getBytes(StandardCharsets.UTF_8); // ✅ FIXED: ensure 16 bytes with UTF-8
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static String encrypt(String data, String key) throws Exception {
        SecretKeySpec secretKey = getKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM); // AES/ECB/PKCS5Padding is default
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedData, String key) throws Exception {
        SecretKeySpec secretKey = getKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
        return new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);
    }
}
