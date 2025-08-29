package vn.mk.eid.web.constant;

public enum FingerKind {
    ROLLED, //-lăn từng ngón (10 ô 1..10)
    PLAIN_SINGLE, //-ấn phẳng 1 ngón (nếu tách riêng)
    PLAIN_RIGHT_FOUR,
    PLAIN_LEFT_FOUR,
    PLAIN_LEFT_FULL,
    PLAIN_RIGHT_FULL;

    public static FingerKind fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return FingerKind.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if you prefer
        }
    }

}
