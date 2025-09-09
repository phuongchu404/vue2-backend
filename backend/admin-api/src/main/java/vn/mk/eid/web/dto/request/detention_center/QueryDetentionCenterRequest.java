package vn.mk.eid.web.dto.request.detention_center;

import lombok.Data;

@Data
public class QueryDetentionCenterRequest {
    private String keyword;
    private String provinceId;
    private String wardId;

    private Integer pageNo;
    private Integer pageSize;
}
