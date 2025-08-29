package vn.mk.eid.web.dto.request;

import lombok.Data;

@Data
public class QueryWardRequest {
    private String keyword;
    private String provinceCode;
    private Integer administrativeUnitId;
}
