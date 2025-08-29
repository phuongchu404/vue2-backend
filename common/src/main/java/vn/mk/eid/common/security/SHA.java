package vn.mk.eid.common.security;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
public class SHA {
    public static String hash(String data, String shaType) {
        return hash(data.getBytes(StandardCharsets.UTF_8), shaType);
    }

    public static String hash(byte[] data, String shaType) {
        try {
            MessageDigest digest = MessageDigest.getInstance(shaType);
            byte[] bytes = digest.digest(data);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xff & bytes[i]);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }

            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String sha1(String data) {
        return hash(data, "SHA-1");
    }

    public static String sha1(byte[] data) {
        return hash(data, "SHA-1");
    }

    public static String sha256(String data) {
        return hash(data, "SHA-256");
    }

    public static String sha256(byte[] data) {
        return hash(data, "SHA-256");
    }
}
