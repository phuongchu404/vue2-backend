package vn.mk.eid.common.util;

import vn.mk.eid.common.icao.ReadEACResponse;
import vn.mk.eid.common.icao.VNIDDG13File;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author mk.com.vn
 * @date 2018/7/24 9:42
 */
public class DigestUtil {
    private static final String defaultAlgorithm = "MD5";
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * @param file
     */
    public static String digest(File file) throws IOException {
        return digest(file, defaultAlgorithm);
    }

    /**
     * @param file
     * @param assignAlgorithm
     */
    public static String digest(File file, String assignAlgorithm) throws IOException {
        if (file == null || !file.exists() || assignAlgorithm == null || assignAlgorithm.length() == 0) {
            return "";
        }
        FileInputStream in = null;
        MappedByteBuffer mappedByteBuffer = null;
        FileChannel fc = null;
        try {
            in = new FileInputStream(file);
            fc = in.getChannel();
            mappedByteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            return digest(mappedByteBuffer, assignAlgorithm);
        } finally {
            unMapBuffer(mappedByteBuffer, fc.getClass());
            if (in != null) {
                in.close();
            }
            if (fc != null) {
                fc.close();
            }
        }
    }

    public static String digest(ByteBuffer resource, String assignAlgorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(assignAlgorithm);
            md.update(resource);
            return bufferToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte[] bytes, int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static void unMapBuffer(MappedByteBuffer buffer, Class channelClass) throws IOException {
        if (buffer == null) {
            return;
        }

        Throwable throwable = null;
        try {
            Method unmap = channelClass.getDeclaredMethod("unmap", MappedByteBuffer.class);
            unmap.setAccessible(true);
            unmap.invoke(channelClass, buffer);
        } catch (NoSuchMethodException e) {
            throwable = e;
        } catch (IllegalAccessException e) {
            throwable = e;
        } catch (InvocationTargetException e) {
            throwable = e;
        }

        if (throwable != null) {
            throw new IOException("MappedByte buffer unmap error", throwable);
        }
    }

    public static String calculateCardInfoHash(List<String> data, ReadEACResponse eacResponse) {
        if (eacResponse != null && eacResponse.getDg13() != null) {
            VNIDDG13File dg13 = eacResponse.getDg13();
//            String s = dg13.buildString();
            return calculateDg13Hash(data, dg13);
        }
        return null;
    }

    public static String calculateDg13Hash(String secretKey, VNIDDG13File dg13) {
        if (dg13 != null) {
            String s = dg13.buildString();
            try {

                MessageDigest md = MessageDigest.getInstance("SHA-256");

                md.update(s.getBytes(StandardCharsets.UTF_8));
                return bufferToHex(md.digest());
            } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
                return null;
            }
        }
        return null;
    }

    public static String calculateDg13Hash(List<String> dataList, VNIDDG13File dg13) {
        if (dg13 != null) {
            String s = dg13.buildString();
            try {
                for (String string: dataList
                     ) {
                    s +=StringUtil.toEmptyString(string);
                }
                MessageDigest md = MessageDigest.getInstance("SHA-256");

                md.update(s.getBytes(StandardCharsets.UTF_8));
                return bufferToHex(md.digest());
            } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
                return null;
            }
        }
        return null;
    }
}
