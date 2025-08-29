package vn.mk.eid.web.controller.user.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UpdateDetail {
    String version;
    boolean isActive;
    Date createdDate;
    String dataFile;
    String signatureFile;

}
