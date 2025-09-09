package vn.mk.eid.web.dto.request.department;

import lombok.Data;

@Data
public class QueryDepartmentRequest {
    private String keyword;
    private Integer detentionCenterId;

    private Integer pageNo;
    private Integer pageSize;
}
