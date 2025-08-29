package vn.mk.eid.common.data;

public enum ResultCode {
    SUCCESS("0", "SUCCESS"),
    IMAGE_MUTI_FACE("59","Image muti face"),
    DATA_NOT_FOUND("050", "DATA NOT FOUND"),
    DATA_NOT_VALID("051", "DATA NOT VALID"),
    REQUEST_EMPTY("052", "REQUEST DATA IS EMPTY"),
    INVALID_SERVER_PRIVATEKEY("053", "Server PrivateKey is invalid or NULL"),
    INVALID_SIGNATURE("054", "Invalid signature"),
    DECRYPTION_FAILED("055", "Decryption failed"),
    INVALID_ACTIVATION_CODE("056", "Invalid activation code"),
    EXPIRED_ACTIVATION_CODE("057", "Expired activation code"),
    USED_ACTIVATION_CODE("058", "Activation code was activated"),

    //    DEVICE_NOT_FOUND("059", "Device not found"),
    INVALID_ACCESS_KEY("060", "Invalid access key"),
    INVALID_CLIENT_PUBLICKEY("061", "Client PublicKey is invalid or NULL"),
    NATIONALITY_NOT_FOUND("062", "Nationality not found"),

    SUCCESS_TWO_STEP("101", "Need 2-step authentication"),
    OTP_VERIFY_FAILED("102", "Verify OTP failed"),
    OTP_GETACTIVATIONCODE_FAILED("103", "Get activation code failed"),
    OTP_IS_ACTIVATION("104", "OTP is activation"),
    OTP_IS_LOCK("105", "OTP is lock"),
    OTP_NOT_FOUND("106", "OTP not found"),
    OTP_EXPIRED("107", "OTP expired"),


    SMS_SEND_SUCCESS("110", "Send SMS success"),
    SMS_SEND_FAILED("111", "Send SMS failed"),
    SMS_INVALID_MOBILE_NUMBER("112", "The mobile number is invalid"),

    INVALID_USER_NAME("120", "Invalid username"),
    VALIDATE_ERROR("121", "Parameter error"),
    VALIDATE_PARAM_ERROR("122", "Parameter error:[%s]"),
    FACE_NOT_FOUND("123", "Face not found"),

    FAILED_TO_GET_READERS_LIST("200", "FAILED TO GET READERS LIST"),
    READER_NOT_FOUND("201", "READER NOT FOUND"),
    NO_CARD("202", "NO CARD"),
    CARD_TIMEOUNT("203", "CARD TIMEOUT"),
    NOT_MATCHED("204", "NOT MATCHED"),
    SAM_READER_NOT_FOUND("205", "SAM READER NOT FOUND"),
    CARD_EXCEPTION("206", "CARD EXCEPTION"),
    READER_BUSY("207", "SPECIFIC READER IS STILL IN USE"),
    READER_NOT_REGISTERED("208","READER IS NOT REGISTERED"),
    FAILED_TO_GET_DEVICES_LIST("209","FAILED TO GET DEVICES LIST"),
    CARD_NOT_FOUND("210", "CARD NOT FOUND"),
    CARD_ALREADY_EXISTED("219", "CARD ALREADY EXISTED"),
    CARD_NOT_EXISTED("230", "CARD NOT EXISTED"),
    CARD_NOT_BORROW("218", "CARD NOT BORROW"),
    // MOC
    VERIFY_MOC_FAILED("211","VERIFY MOC FAILED"),
    VERIFY_MOC_ERROR("212","VERIFY MOC ERROR"),
    CREATE_TEMPLATE_ERROR("213","CREATE TEMPLATE ERROR"),
    FAILED_TO_GET_DEVICE_INFO("214","FAILED TO GET DEVICE INFO"),
    NO_TEMPLATE("215","NO TEMPLATE"),
    LEFT_FINGER_NO_TEMPLATE("216","VERIFY MOC FAILED, NO LEFT FINGER TEMPLATE"),
    RIGHT_FINGER_NO_TEMPLATE("217","VERIFY MOC FAILED, NO RIGHT FINGER TEMPLATE"),

    EMPTY_USERNAME("220", "Username cannot be empty"),
    EMPTY_PASSWORD("221", "Password cannot be empty"),
    EMPTY_ACCESS_TOKEN("222", "Access token cannot be empty"),
    EMPTY_ROLENAME("223", "Role name cannot be empty"),
    EMPTY_DESCRIPTION("224", "Role description cannot be empty"),
    READ_INFO_FAILED("225", "READ INFO FAILED"),
    IMAGE_QUALITY_NOT_ENOUGH("226", "IMAGE QUALITY IS NOT ENOUGH"),

    INVALID_USERNAME("250", "Invalid username."),
    INVALID_PASSWORD("251", "Invalid password."),
    INVALID_ACCESS_TOKEN("252", "Invalid access token."),
    INVALID_ROLENAME("253", "Invalid role name"),
    INVALID_DESCRIPTION("254", "Invalid role description"),


    INVALID_USER_ID("280", "Invalid user id."),
    INVALID_ROLE_ID("281", "Invalid role id."),
    INVALID_PERMISSION_ID("282", "Invalid permission id"),
    PERMISSION_NOT_FOUND("297", "Permission not found"),
    USER_NOT_ACCESS("299", "User not access."),

    USER_NOT_FOUND("300", "User not found."),
    LOGIN_INFO_INCORRECT("301", "Login info is incorrect"),
    INCORRECT_PASSWORD("302", "Incorrect password."),
    CONS_PASS_FAULTY("303", "Password error exceeds 3 times."),
    CONS_OTP_FAULTY("304", "OTP error exceeds 3 times."),
    USER_ALREADY_EXISTED("305", "User already existed."),
    INCORRECT_OTP("306", "Incorrect code."),
    ROLE_ALREADY_EXISTED("307", "Role already existed."),
    ROLE_HAS_USERS("308", "Role has already assigned to the user"),
    ORDER_COMPLETED("313", "Order already completed."),
    USER_CANNOT_BE_DELETED("314", "User cannot be deleted"),
    ROLE_CANNOT_BE_DELETED("315", "Role cannot be deleted"),


    ROLE_CANNOT_BE_UPDATE("326", "Role cannot be update"),
    USER_NOT_ROLE("327", "User not Role."),
    ROLE_NOT_FOUND("328", "Role not found."),
    USER_CANNOT_BE_UPDATE("329", "Role cannot be update"),

    CUSTOMER_ALREADY_EXISTED ("330", "Visitor already existed"),
    CUSTOMER_NOT_FOUND("331", "Visitor not found"),
    CUSTOMER_NOT_EXISTED("332", "Visitor not existed"),
    PROVIDER_NOT_EXISTED("333", "Provider not existed"),
    BRANCH_NOT_EXISTED("334", "Branch not existed"),
    BRANCH_ALREADY_EXISTED("335", "Branch already existed"),
    CUSTOMER_EXCEED_LIMIT("336", "Total customers exceed the limit"),
    CUSTOMER_INVALID("337", "Invalid customer"),
    CUSTOMER_BORROW_CARD("338","Customer borrow card"),

    EXPIRED_TOKEN("401", "Access token expired."),
    NO_ACCESS_RIGHT("402", "No right"),

    //Callback
    CALLBACK_FAILED("450", "Callback failed"),

    TIME_NOT_CORRECT("501", "Time not correct"),
    // Device
    DEVICE_REG_ERROR("502", "Register device error"),
    DEVICE_NOT_REG_BEFORE("503", "Device not reg before"),
    EXTRACT_TEMPLATE_FAILED("504", "Failed to extract template"),
    DEVICE_REG_INFO_NOT_MATCH("505", "Device register info not match"),
    DEVICE_ALREADY_EXISTED("506", "Device existed"),
    SERVER_SIGNATURE_VERIFY_FAILED("507", "Verify server signature failed"),
    GET_SERVER_CERT_FAILED("508", "Cannot get server certificate"),
    DEVICE_IS_NOT_ACTIVATED("509", "Device is not activated"),
    DEVICE_NOT_FOUND("510", "Device not found"),
    SEND_SERVER_FAILED("511", "SEND TO SERVER FAILED"),
    MIDDLEWARE_SIGNATURE_VERIFY_FAILED("512", "Verify middleware signature failed"),

    REQUEST_ID_ALREADY_EXIST("520", "RequestId already exists"),

    //SMART CARD
    SC_CANNOT_SELECT_APP("550", "Cannot select App"),
    SC_CANNOT_SEND_DATA("551", "Cannot send data to card"),
    SC_AUTHEN_FAILED("552", "Authentication to card failed"),

    //ICAO:
    INVAVID_SIGNATURE_SOD("600", "FAILED TO VERIFY SOD SIGNATURE"),
    FAILED_TO_VERIFY_SOD("601", "FAILED TO VERIFY SOD"),
    CARD_AUTH_FAILED("602", "CARD AUTHENTICATION FAILED"),
    CARD_AA_FAILED("603", "AA FAILED"),
    CARD_CA_FAILED("604", "CA FAILED"),
    CARD_TA_FAILED("605", "TA FAILED"),


    //Division
    DIVISION_ALREADY_EXISTED("701", "Division already existed."),
    DIVISION_NOT_EXISTS("702", "Division not exists."),

    //Info
    INFO_ALREADY_EXISTED("703", "Info already existed."),
    INFO_NOT_EXISTS("704", "Info not exists."),


    //Certificate
    CERTIFICATE_ALREADY_EXISTED("705", "Certificate already existed."),
    CERTIFICATE_NOT_EXISTS("706", "Certificate not exists."),
    DELETE_CERTIFICATE_FAILED("707", "FAILED TO DELETE CERTIFICATE"),

    //
    NO_ROLE_DO_ACTION("708", "User do not have role to do action!"),
    CODE_DEPARTMENT_ALREADY_EXISTED("709", "Code department already existed"),
    DEPARTMENT_DONT_EXISTED("710", "Department don't existed"),
    DELETE_GROUP_FAILED("711", "DEPARTMENT HAVE CHILDREN"),
    //Info
    BLACKLIST_NOT_EXISTED("712", "Person not exists"),
    BLACKLIST_ALREADY_EXISTS("713", "Person already exists."),

    // upload excel
    UPLOAD_FILE_EXCEL_FAILED("714", "Update file excel failed"),
    EXIST_FILE_EXCEL("715", "File already exists"),
    DATE_VALIDATE("716", "Date validate error"),
    DEVICE_SERIAL_NOT_FOUND("717", "Device serial not found"),
    DEVICE_SERIAL_ALREADY_EXISTED("718", "Device serial already existed"),
    DEVICE_SERIAL_NOT_FOUNT("719","Device serial not fount"),

    //Units
    UNITS_NOT_EXISTED("720", "Units not existed."),
    UNITS_ALREADY_EXISTED("721", "Units already existed."),

    //IPS
    IPS_AN_TEMPLATE_PARSE_FAILED("800", "FAILED TO PARSE TEMPLATE"),
    IPS_FAILED_TO_ASSESS_QUALITY("801", "FAILED TO ASSESS QUALITY"),


    FAILED_TO_PERFORM_OPERATION("991", "Failed to perform operation"),
    LICENSE_ERROR("993", "LICENSE ERROR"),
    INVOKE_CORE_ERROR("994", "INVOKE CORE ERROR: "),
    JOB_EXECUTE_ERROR("995", "JOB EXECUTE ERROR"),
    SERVER_ERROR("996", "SERVER ERROR"),
    IO_ERROR("997", "IO ERROR"),
    DATABASE_ERROR("998", "DATABASE ERROR"),
    UNKNOWN_ERROR("999", "UNKNOWN ERROR"),

    VERSION_ALREADY_EXISTS("1000", "VERSION ALREADY EXISTS"),
    ACTIVE_VERSION_CANNOT_BE_DELETED("1001", "ACTIVE VERSION CANNOT BE DELETED"),
    MAXIMUM_UPDATES_EXCEEDED("1002", "EXCEED MAXIMUM UPDATES"),
    DEVICE_REG_EXISTED("1004", "Device register existed"),

    COULD_NOT_OBTAIN_SDK_LICENSE("1008", "Could not obtain SDK license"),
    LICENSE_DEVICE_LIMITED("1009", "Number of device is limited"),
    NO_MOC_RESPONSE("1010", "No moc response"),
    NO_MRZ_RESPONSE("1011", "No mrz response"),
    NO_FACE_TEMPLATE_RESPONSE("1012", "No face template response"),
    NO_FINGER_TEMPLATE_RESPONSE("1013", "No finger template response"),
    NO_VERIFY_SOD_RESPONSE("1014", "No SOD response"),


    CAMERA_NOT_FOUND("1015", "Camera not found"),
    FINGER_SCANNER_NOT_FOUND("1016", "Finger scanner not found"),


    DEVICE_NOT_SUPPORTED("2001", "Device not supported"),
    LIVENESS_FAKE_FACE("2002", "Check face liveness failed"),
    CAN_NOT_GET_SERIAL_NUMBER("2003", "Check face liveness failed"),
    SAM_SELECT_APPLET_FAILED("2004", "Sam - select applet failed"),
    SAM_SET_SERIAL_FAILED("2005", "Sam - set serial failed"),
    SAM_GET_PUBKEY_FAILED("2006", "Sam - get public key failed"),
    SAM_GET_RANDOM_FAILED("2007", "Sam - get random failed"),
    SAM_VERIFY_SERIAL_FAILED("2008", "Sam - verify serial failed"),
    VERSION_ALREADY_EXISTED("2010", "version already existed"),
    EXIST_PROVIDER("2011", "provider existed"),
    INVALID_TRANSACTION_HASH("2012", "INVALID HASH"),
    CANNOT_GET_PUBLICKEY("2013","CAN NOT GET SERVER PUBLIC KEY"),
    INIT_MRZ_READER_FAILED("3000", "Init MRZ Reader failed" ),
    CLOSE_MRZ_READER_FAILED("3001", "Close MRZ Reader failed"),
    VISITOR_CARD_NOT_FOUND("3002","VISITOR CARD NOT FOUND"),
    VISITOR_NOT_FOUND("3005","VISITOR NOT FOUND"),
    FAILED_TO_ENROLL("3003","FAILED TO ENROLL"),
    FAILED_TO_IDENTIFY("3004","FAILED TO IDENTIFY")
    ;


    private String code;
    private String description;
//    private String extraMessage;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExtraMessage(String extraMessage) {
        this.description = this.description + " " + extraMessage;
    }

    public static ResultCode getByCode(String code) {
        for(ResultCode e : values()) {
            if(e.code.equals(code)) return e;
        }
        return UNKNOWN_ERROR;
    }

    ResultCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
