package vn.mk.eid.web.dto.request;

import lombok.Data;

@Data
public class QueryProvinceRequest {
    private String keyword;
    private Integer administrativeUnitId;
}
