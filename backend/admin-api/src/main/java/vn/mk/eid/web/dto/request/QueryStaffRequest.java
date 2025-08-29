package vn.mk.eid.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryStaffRequest {
    private String staffCode;
    private String fullName;
    private String rank;
    private String status;
}
