package vn.mk.eid.web.dto.request.staff;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StaffUpdateRequest extends StaffCreateRequest {
    private String status;
}
