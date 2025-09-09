package vn.mk.eid.web.dto.request.detainee;

import lombok.Data;

@Data
public class QueryDetaineeRequest {
//    private String keyword;
    private String detaineeCode;
    private String fullName;
    private String idNumber;
    private String status;
    private Integer detentionCenterId;
}
