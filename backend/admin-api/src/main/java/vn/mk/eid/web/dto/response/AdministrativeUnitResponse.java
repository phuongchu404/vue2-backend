package vn.mk.eid.web.dto.response;

import lombok.Data;

@Data
public class AdministrativeUnitResponse {
    private Integer id;
    private String fullName;
    private String fullNameEn;
    private String shortName;
    private String shortNameEn;
}
