package vn.mk.eid.common.security;

import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class AES {

    public static int n = 15;

    public static String randomString() {
        String CharNumericString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789012345678901234567890123456789";
        Random rd = new Random();
        StringBuilder string = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            string.append(CharNumericString.charAt(rd.nextInt(CharNumericString.length())));
        }
        return string.toString();
    }

    public static String Encrypt2(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Hash key.
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes(StandardCharsets.UTF_8);
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);

        // set IV
        byte[] iv = {0x4D, 0x6B, 0x67, 0x72, 0x6F, 0x75, 0x70, 0x40, 0x31, 0x39, 0x39, 0x21, 0x32, 0x30, 0x31, 0x36};

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        //text = randomString() + text;
        byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String Encrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Hash key.
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes(StandardCharsets.UTF_8);
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);

        // set IV
        byte[] iv = {0x4D, 0x6B, 0x67, 0x72, 0x6F, 0x75, 0x70, 0x40, 0x31, 0x39, 0x39, 0x21, 0x32, 0x30, 0x31, 0x36};

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static byte[] EncryptByte(byte[] text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Hash key.
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes(StandardCharsets.UTF_8);
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);

        // set IV
        byte[] iv = {0x4D, 0x6B, 0x67, 0x72, 0x6F, 0x75, 0x70, 0x40, 0x31, 0x39, 0x39, 0x21, 0x32, 0x30, 0x31, 0x36};

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] encryptedBytes = cipher.doFinal(text);

        return encryptedBytes;
    }

    public static String Decrypt2(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes(StandardCharsets.UTF_8);
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        //
        byte[] iv = {0x4D, 0x6B, 0x67, 0x72, 0x6F, 0x75, 0x70, 0x40, 0x31, 0x39, 0x39, 0x21, 0x32, 0x30, 0x31, 0x36};
        //
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        //Base64 decoder = new Base64();
        byte[] results = cipher.doFinal(Base64.getDecoder().decode(text));

        String data = new String(results, StandardCharsets.UTF_8);
        return data.substring(n);
    }

    public static String Decrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes(StandardCharsets.UTF_8);
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        //
        byte[] iv = {0x4D, 0x6B, 0x67, 0x72, 0x6F, 0x75, 0x70, 0x40, 0x31, 0x39, 0x39, 0x21, 0x32, 0x30, 0x31, 0x36};
        //
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        //Base64 decoder = new Base64();
        byte[] results = cipher.doFinal(Base64.getDecoder().decode(text));

        return new String(results, StandardCharsets.UTF_8);
    }

    public static byte[] DecryptByte(byte[] text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes(StandardCharsets.UTF_8);
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        //
        byte[] iv = {0x4D, 0x6B, 0x67, 0x72, 0x6F, 0x75, 0x70, 0x40, 0x31, 0x39, 0x39, 0x21, 0x32, 0x30, 0x31, 0x36};
        //
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        //Base64 decoder = new Base64();
        byte[] results = cipher.doFinal(text);

        return results;
    }

    public static String Encrypt(String text, String key, int keySize) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        // Generating IV.
//        int ivSize = 16;
//        byte[] iv = new byte[ivSize];
//        SecureRandom random = new SecureRandom();
//        random.nextBytes(iv);
//        IvParameterSpec ivSpec = new IvParameterSpec(iv);
//
//        // Hash key.
//        byte[] keyBytes = new byte[keySize];
//        byte[] b = key.getBytes(StandardCharsets.UTF_8);
//        int len = b.length;
//        if (len > keyBytes.length) len = keyBytes.length;
//        System.arraycopy(b, 0, keyBytes, 0, len);
//        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
//
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//
//        // Encrypt
//        byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
//
//        // Combine IV and encrypted part.
//        byte[] encryptedIVAndText = new byte[ivSize + encryptedBytes.length];
//        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
//        System.arraycopy(encryptedBytes, 0, encryptedIVAndText, ivSize, encryptedBytes.length);
        byte[] encryptedIVAndText = Encrypt2Byte(text, key, keySize);
        return Base64.getEncoder().encodeToString(encryptedIVAndText);
    }

    public static String Encrypt(String text, byte[] keyBytes, int keySize) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        // Generating IV.
//        int ivSize = 16;
//        byte[] iv = new byte[ivSize];
//        SecureRandom random = new SecureRandom();
//        random.nextBytes(iv);
//        IvParameterSpec ivSpec = new IvParameterSpec(iv);
//
//        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
//
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//
//        // Encrypt
//        byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
//
//        // Combine IV and encrypted part.
//        byte[] encryptedIVAndText = new byte[ivSize + encryptedBytes.length];
//        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
//        System.arraycopy(encryptedBytes, 0, encryptedIVAndText, ivSize, encryptedBytes.length);
        byte[] encryptedIVAndText = Encrypt2Byte(text, keyBytes, keySize);
        return Base64.getEncoder().encodeToString(encryptedIVAndText);
    }

    public static byte[] Encrypt2Byte(String text, String key, int keySize) throws Exception {
        byte[] keyBytes = new byte[keySize];
        byte[] b = key.getBytes(StandardCharsets.UTF_8);
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);

        return Encrypt2Byte(text, keyBytes, keySize);
    }

    public static byte[] Encrypt2Byte(String text, byte[] keyBytes, int keySize) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // Generating IV.
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        // Encrypt
        byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted part.
        byte[] encryptedIVAndText = new byte[ivSize + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
        System.arraycopy(encryptedBytes, 0, encryptedIVAndText, ivSize, encryptedBytes.length);

        return (encryptedIVAndText);
    }

    public static String Encrypt2Hex(String text, String key, int keySize) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        // Generating IV.
//        int ivSize = 16;
//        byte[] iv = new byte[ivSize];
//        SecureRandom random = new SecureRandom();
//        random.nextBytes(iv);
//        IvParameterSpec ivSpec = new IvParameterSpec(iv);
//
//        // Hash key.
//        byte[] keyBytes = new byte[keySize];
//        byte[] b = key.getBytes(StandardCharsets.UTF_8);
//        int len = b.length;
//        if (len > keyBytes.length) len = keyBytes.length;
//        System.arraycopy(b, 0, keyBytes, 0, len);
//        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
//
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//
//        // Encrypt
//        byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
//
//        // Combine IV and encrypted part.
//        byte[] encryptedIVAndText = new byte[ivSize + encryptedBytes.length];
//        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
//        System.arraycopy(encryptedBytes, 0, encryptedIVAndText, ivSize, encryptedBytes.length);

        byte[] encryptedIVAndText = Encrypt2Byte(text, key, keySize);
        return Hex.toHexString(encryptedIVAndText);
    }

    public static String Encrypt2Hex(String text, byte[] keyBytes, int keySize) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        // Generating IV.
//        int ivSize = 16;
//        byte[] iv = new byte[ivSize];
//        SecureRandom random = new SecureRandom();
//        random.nextBytes(iv);
//        IvParameterSpec ivSpec = new IvParameterSpec(iv);
//
//        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
//
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//
//        // Encrypt
//        byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
//
//        // Combine IV and encrypted part.
//        byte[] encryptedIVAndText = new byte[ivSize + encryptedBytes.length];
//        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
//        System.arraycopy(encryptedBytes, 0, encryptedIVAndText, ivSize, encryptedBytes.length);
//
//        return Hex.toHexString(encryptedIVAndText);
        byte[] encryptedIVAndText = Encrypt2Byte(text, keyBytes, keySize);
        return Hex.toHexString(encryptedIVAndText);

    }

    public static byte[] Decrypt2Byte(String encryptedText, byte[] keyBytes, int keySize) throws Exception {
        int ivSize = 16;

        byte[] encryptedIvTextBytes = Base64.getDecoder().decode(encryptedText);

        // Extract IV.
        byte[] iv = new byte[ivSize];
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted part.
        int encryptedSize = encryptedIvTextBytes.length - ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Decrypt.
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

        return decrypted;
    }

    public static byte[] Decrypt2Byte(String encryptedText, String key, int keySize) throws Exception {
        // Hash key.
        byte[] keyBytes = new byte[keySize];
        byte[] b = key.getBytes(StandardCharsets.UTF_8);
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        return Decrypt2Byte(encryptedText, keyBytes, keySize);
    }

    public static String Decrypt(String encryptedText, String key, int keySize) throws Exception {
        byte[] decrypted = Decrypt2Byte(encryptedText, key, keySize);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static String Decrypt(String encryptedText, byte[] keyBytes, int keySize) throws Exception {
        byte[] decrypted = Decrypt2Byte(encryptedText, keyBytes, keySize);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static String Decrypt2Hex(String encryptedText, String key, int keySize) throws Exception {
        byte[] decrypted = Decrypt2Byte(encryptedText, key, keySize);
        return Hex.toHexString(decrypted);
    }

    public static String Decrypt2Hex(String encryptedText, byte[] keyBytes, int keySize) throws Exception {
        byte[] decrypted = Decrypt2Byte(encryptedText, keyBytes, keySize);
        return Hex.toHexString(decrypted);
    }
}
