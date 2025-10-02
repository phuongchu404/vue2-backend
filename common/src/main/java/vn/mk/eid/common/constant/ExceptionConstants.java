package vn.mk.eid.common.constant;

public class ExceptionConstants {
    public static final String TIME_INVALID = "Time is not in correct format";
    public static final String EMAIL_INVALID = "Email is not in correct format";

    // detention_center
    public static final String DETENTION_CENTER_NOT_NULL = "Detention center is not null";
    public static final String DETENTION_CENTER_NOT_FOUND = "Detention Center not found";
    public static final String DUPLICATE_DETENTION_CENTER_CODE = "Detention Center Code already exists";
    public static final String DETENTION_CENTER_CAPACITY_INVALID = "Capacity cannot be less than Current Population";

    // detainee
    public static final String DETAINEE_NOT_FOUND = "Detainee not found";
    public static final String DETAINEE_CODE_NOT_NULL = "Detainee Code cannot be null";
    public static final String DETAINEE_NOT_FOUND_WITH_CODE = "Detainee not found with code: %s";

    // department
    public static final String DEPARTMENT_NAME_NOT_NULL = "Department Name cannot be blank";
    public static final String DEPARTMENT_NOT_FOUND = "Department not found";

    // staff
    public static final String STAFF_NAME_NOT_NULL = "Staff name is not null";
    public static final String IDENTITY_INVALID = "Identity is invalid";
    public static final String ISSUE_PLACE_INVALID = "Issue place is invalid";
    public static final String DUPLICATE_ID_NUMBER = "Staff with this Id Number already exists";
    public static final String STAFF_NOT_FOUND = "Staff not found";
    public static final String ETHNICITY_NOT_FOUND = "Ethnicity not found";
    public static final String POSITION_NOT_FOUND = "Position not found";
    public static final String EDUCATION_LEVEL_NOT_FOUND = "Education Level not found";

    // identity record
    public static final String DUPLICATE_IDENTITY_RECORD = "This detainee already has an identity record";
    public static final String IDENTITY_RECORD_NOT_FOUND = "This detainee already has an identity record";

    // fingerprint
    public static final String FINGER_PRINT_NOT_FOUND = "Finger Print not found";
    public static final String DUPLICATE_FINGER_PRINT_IMPRESSION = "This detainee already has a finger print impression";
    public static final String DUPLICATE_DEPARTMENT_CODE = "DUPLICATE DEPARTMENT CODE";
}
