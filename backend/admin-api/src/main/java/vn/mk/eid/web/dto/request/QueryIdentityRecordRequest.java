package vn.mk.eid.web.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryIdentityRecordRequest {
    private String detaineeCode;
    private String detaineeName;
    private String arrestUnit;
}
