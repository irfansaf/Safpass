package com.irfansaf.safpass.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Random;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class CryptUtils {

    private CryptUtils() {
        // Utility Class
    }

    /**
     * Generates key with {@link #getPBKDF2Key(char[], byte[], int) with default iterations}
     *
     * <p>
     *     In 2021, OWASP recommend to use 310,000 iterations for
     *     PBKDF2-HMAC-SHA256
     * </p>
     * @param text
     * @param salt
     * @return
     */
    public static byte[] getPBKDF2KeyWithDefaultIterations(final char[] text,final byte[] salt) {
        return getPBKDF2Key(text, salt, 310_100);
    }

    /**
     * Generates key with PBKDF2 (Password-Based Key Derivation Function 2).
     *
     * @param text
     * @param salt
     * @param iteration
     * @return
     */
    private static byte[] getPBKDF2Key(final char[] text, final byte[] salt, final int iteration) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(text, salt, iteration, 256);
            SecretKey secretKey = factory.generateSecret(spec);
            return secretKey.getEncoded();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Could not generate PBKDF2-HMAC-SHA256 key: " + e.getMessage());
        }
    }

    public static byte[] getSha256HashWithDefaultIterations(final char[] text) {
        return getSha256Hash(text, 1000);
    }

    private static byte[] getSha256Hash(final char[] text) {
        return getSha256Hash(text, 0);
    }

    private static byte[] getSha256Hash(final char[] text, final int iteration) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            // messageDigest.update(salt);
            byte[] bytes = new String(text).getBytes(StandardCharsets.UTF_8);
            byte[] digest = messageDigest.digest(bytes);
            for (int i = 0; i < iteration; i++) {
                messageDigest.reset();
                digest = messageDigest.digest(digest);
            }
            return digest;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Could not generate SHA-256 key: " + e.getMessage());
        }
    }
}
