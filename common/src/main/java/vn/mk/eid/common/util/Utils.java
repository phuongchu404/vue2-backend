package vn.mk.eid.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@Slf4j
public class Utils {

//    public static byte[] decodeBase64Image(String imageStr) {
//        byte[] bytes;
//        if (imageStr.startsWith(Constants.BASE64_IMAGE)) {
//            bytes = Base64.decodeBase64(imageStr.split(",")[1]);
//        } else {
//            bytes = Base64.decodeBase64(imageStr);
//        }
//        return bytes;
//    }

    public static String encodeBase64Image(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }


    public static Integer parseInt(String value, Integer defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.error("Failed to parse Integer value: " + value);
            return defaultValue;
        }
    }

    public static Integer parseInt(String value) {
        return parseInt(value, 0);
    }



    public static Long parseLong(String value, Long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.error("Failed to parse Long value: " + value);
            return defaultValue;
        }
    }

    public static Long parseLong(String value) {
        return parseLong(value, 0L);
    }

    public static Date addDay(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public static Date addMonth(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    public static <T> T parseJsonWithTime(Object object, Class<T> type) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        String jsonString = gson.toJson((HashMap<String, JsonObject>) object);
        return gson.fromJson(jsonString, type);
    }

    public static <T> T parseJsonWithNull(String data, Class<T> object) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        T t = gson.fromJson(data, object);
        return t;
    }

    public static <T> T parseJson(String data, Class<T> object) {
        Gson gson = new Gson();
        T t = gson.fromJson(data, object);
        return t;
    }

    public static  String toJsonString(Object object) {
        Gson gson = new Gson();
        String s = gson.toJson(object);
        return s;
    }

    public static Date parseDate(String dateStr, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(dateStr);
    }

    public static boolean isBlankString(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static String generateRandom(long max, long min) {
        long rn = 0;
        rn = (long) (Math.random() * ((max - min) + 1)) + min;
        return String.valueOf(rn);
    }
}
