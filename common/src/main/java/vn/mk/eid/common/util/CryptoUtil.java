package vn.mk.eid.common.util;

import org.apache.commons.codec.binary.Base32;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author mk.com.vn
 * @date 2017/10/24
 */
public class CryptoUtil {
    public static String encryptPassword(String password) {
        String salt = BCrypt.gensalt(10);
        return BCrypt.hashpw(password, salt);
    }

    public static boolean verifyPassword(String password, String encryptPassword) {
        return BCrypt.checkpw(password, encryptPassword);
    }

    public static String getRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    public static String randomDigits(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(new Random().nextInt(10));
        }
        return result.toString();
    }

    public static String getRandomSecretKey(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }


    public static String randomText(int length) {
        StringBuilder result = new StringBuilder();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        List<String> data = new ArrayList<String>(Arrays.asList(characters.split("")));
        Integer size = data.size();
        for (int i = 0; i < length; i++) {
            Integer index = new Random().nextInt(size);
            result.append(data.get(index));
        }
        return result.toString();
    }
}
