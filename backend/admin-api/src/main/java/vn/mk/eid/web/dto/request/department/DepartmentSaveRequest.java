package vn.mk.eid.web.dto.request.department;

import lombok.Data;
import vn.mk.eid.common.constant.ExceptionConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DepartmentSaveRequest {
    @NotBlank(message = ExceptionConstants.DEPARTMENT_NAME_NOT_NULL)
    private String name;
    @NotNull(message = ExceptionConstants.DETENTION_CENTER_NOT_NULL)
    private Integer detentionCenterId;
    private String description;
    private Boolean isActive = Boolean.TRUE;
}
