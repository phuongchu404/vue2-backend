package vn.mk.eid.web.dto.request;

import lombok.Data;

@Data
public class QueryFingerPrintRequest {
    private String detaineeName;
    private String detaineeCode;
    private Integer detentionCenterId;
}
