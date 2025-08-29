package vn.mk.eid.web.dto.request;

import lombok.Data;

@Data
public class QueryUnitRequest {
    private String keyword;
    private Integer provinceId;
    private Integer wardId;
}
