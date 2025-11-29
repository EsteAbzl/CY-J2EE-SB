package com.util;

import java.security.MessageDigest;
import java.util.Base64;

public class PasswordUtil {
    public static String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(raw.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(dig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean matches(String raw, String storedHash) {
        return hash(raw).equals(storedHash);
    }
}
