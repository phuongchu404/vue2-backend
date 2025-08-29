package vn.mk.eid.common.response;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author mk
 * @date 05-Aug-2025
 */
@Data
@AllArgsConstructor
public class PermissionVO {
    String tag;
    Integer removable;
}
