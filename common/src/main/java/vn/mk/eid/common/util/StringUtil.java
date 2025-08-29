package vn.mk.eid.common.util;

public class StringUtil {

    public static int getNumberFromString(String source,String specifiedStr){
        if(isEmpty(source) || isEmpty(specifiedStr)) return 0;
        return (source.length() - source.replaceAll(specifiedStr,"").length())/specifiedStr.length();
    }

    public static boolean isEmpty(String source){
        return source==null || source.length() == 0;
    }

    public static String toEmptyString(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
