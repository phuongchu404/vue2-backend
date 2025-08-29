package vn.mk.eid.common.util;

import vn.mk.eid.common.security.ECC;

public class EncryptionUtil {
    static byte[] secret = new byte[]{109, 35, 75, 64, 56, 120, 111, 57, 104, 118};//"m#K@8xo9hv";

    static String ECC_ENCRYPTION_PUBLICKEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAERxIqmQqOE9UXdVHh/42oSx9T0Pm6" +
            "+5LbBFlKITQYQ6vjx/dV/bjFkSse0CUfFToV1fTuwZyTlzoJlPlF2utf5g==";

    static String ECC_ENCRYPTION_PRIVATEKEY = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCAEQr63OFTELYyihE+b" +
            "X/51eWuf6xYFi+EABYdvi68C5w==";

    public static String ecc_EncryptData(String data) {
        String encData = "";
        byte[] bytesData = data.getBytes();
        byte[] bytes = new byte[bytesData.length + secret.length];
        for (int i = 0; i < bytesData.length; i++) {
            bytes[i] = (byte) (bytesData[i] + (byte) i);
        }
        for (int i = bytesData.length; i < bytes.length; i++) {
            bytes[i] = secret[i - bytesData.length];
        }
        encData = ECC.encrypt(bytes, ECC_ENCRYPTION_PUBLICKEY);

        return encData;
    }

    public static String ecc_DecryptData(String data) {
        String decData = "";
        byte[] bytes = ECC.decrypt2Bytes(data, ECC_ENCRYPTION_PRIVATEKEY);
        byte[] bytesData = bytes2Bytes(bytes, bytes.length - secret.length);
        for (int i = 0; i < bytesData.length; i++) {
            bytesData[i] = (byte) (bytesData[i] - (byte) i);
        }
        decData = new String(bytesData);

        return decData;
    }

    public static byte[] bytes2Bytes(byte[] data, int size) {
        byte[] result = new byte[size];
        if (data == null) {
            return result;
        }

        if (size <= data.length) {
            for (int i = 0; i < size; i++) {
                result[i] = data[i];
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i];
            }
        }

        return result;
    }
}
