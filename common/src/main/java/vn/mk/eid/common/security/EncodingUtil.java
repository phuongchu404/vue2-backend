package vn.mk.eid.common.security;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncodingUtil {
    public static String utf8base64(String data) {
        String b64 = "";
        try {
            b64 = java.util.Base64.getEncoder().encodeToString(data.getBytes("utf-8"));
        } catch (Exception ex) {
            log.error("utf82base64 Exception: " + ex.getMessage());
            return data;
        }
        return b64;
    }

    public static String base64utf8(String data) {
        String utf8 = "";
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(data);
            utf8 = new String(bytes, "utf-8");
        } catch (Exception ex) {
            log.error("base64utf8 Exception: " + ex.getMessage());
            return data;
        }
        return utf8;
    }

    public static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char ch : chars) {
            sb.append(Integer.toHexString((int) ch));
        }
        return sb.toString();
    }

    public static String hexToAscii(String hexStr) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            sb.append((char) Integer.parseInt(str, 16));
        }
        return sb.toString();
    }

    public static String bytes2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }


}
