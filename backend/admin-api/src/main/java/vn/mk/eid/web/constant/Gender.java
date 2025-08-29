package vn.mk.eid.web.constant;

import vn.mk.eid.web.utils.StringUtil;

public enum Gender {
    NAM(0, "NAM"),
    NU(1, "NU"),
    OTHER(2, "OTHER");
    private final Integer id;
    private final String code;

    Gender(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    // get code by id
    public static String getCodeById(Integer id) {
        if (id == null) {
            return null;
        }
        for (Gender gender : Gender.values()) {
            if (gender.getId().equals(id)) {
                return gender.getCode();
            }
        }
        return OTHER.getCode(); //default OTHER code
    }

    // get id by code
    public static Integer getIdByCode(String code) {
        if (StringUtil.isBlank(code)) {
            return null;
        }
        for (Gender gender : Gender.values()) {
            if (gender.getCode().equals(code)) {
                return gender.getId();
            }
        }
        return OTHER.getId(); //default OTHER code
    }

    // get enum by id
    public static Gender fromId(Integer id) {
        for (Gender gender : Gender.values()) {
            if (gender.getId().equals(id)) {
                return gender;
            }
        }
        return OTHER; // default  OTHER
    }
}
