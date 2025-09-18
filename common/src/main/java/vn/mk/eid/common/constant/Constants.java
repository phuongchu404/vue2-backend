package vn.mk.eid.common.constant;


public class Constants {

    public static final String BASE64_IMAGE = "data:image";
    public static final String FRONTEND_DATE_FORMAT = "dd/MM/yyyy";
    public static final String C_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String C_YYYY_MM = "yyyy-MM";
    public static final String C_MM_YYYY = "MM/yyyy";

    public static final String NO_FINGER_HASH_CODE = "0e0b38ee304600afa74f81f868c6c364e0392e40";

    public static final String SAM_NOT_REGISTERED = "notregistered";

    public static final long UNLIMITED = -1;

    public static final int MOC_TYPE_FACE = 1;

    public static final int MOC_TYPE_FINGER_LEFT = 2;

    public static final int MOC_TYPE_FINGER_RIGHT = 3;

    public static final int DATA_TYPE_IMAGE = 0;

    public static final int DATA_TYPE_TEMPLATE = 1;

    public static final int ICAO_TYPE_BAC = 1;

    public static final int ICAO_TYPE_EAC = 0;


    public static final int BIO_TYPE_FACE = 1;

    public static final int BIO_TYPE_FINGER = 2;

    public static final int EDOC_AUTHEN_TYPE_HSM = 1;

    public static final int EDOC_AUTHEN_TYPE_SAM = 2;

    public static class ReportRedisKey {
        public static final String TOTAL_DETAINEE_PREVIOUS = "TOTAL_DETAINEE_PREVIOUS_";
        public static final String TOTAL_STAFF_PREVIOUS = "TOTAL_STAFF_PREVIOUS_";
        public static final String TOTAL_IDENTITY_PREVIOUS = "TOTAL_IDENTITY_PREVIOUS_";
        public static final String TOTAL_FINGER_PREVIOUS = "TOTAL_FINGER_PREVIOUS_";
    }

    public static class CodePrefix {
        public static final String STAFF_CODE = "CB";
        public static final String DETAINEE_CODE = "DN";
        public static final String DETENTION_CENTER = "TG";
        public static final String DEPARTMENT = "PB";
    }

    public static class TableName {
        public static final String STAFF = "staff";
        public static final String DETAINEE = "detainees";
        public static final String DETENTION_CENTER = "detention_centers";
        public static final String DEPARTMENT = "departments";
    }

    public static class RedisKey {
        public static final String GEN_CODE = "gen_code_seq";
    }
}
