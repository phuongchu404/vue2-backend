package vn.mk.eid.web.dto.request;

import lombok.Data;

@Data
public class QueryEducationLevelRequest {
    private String keyword;

    private Integer pageNo;
    private Integer pageSize;
}
