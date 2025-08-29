package vn.mk.eid.common.data;


/**
 * @author mk
 * @date 05-Aug-2025
 */
public enum RemovableEnum {
    REMOVE(1),
    NOTREMOVE(0);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    private RemovableEnum(int code) {
        this.code = code;
    }
}