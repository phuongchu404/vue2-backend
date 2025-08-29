package vn.mk.eid.common.data;

public enum AuthenType {
    NO_DEFINE(0, ""),
    FACE(1, "Authentication by FACE"),
    FINGER(2, "Authentication by FINGER")
    ;

    private Integer code;
    private String description;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private AuthenType(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
