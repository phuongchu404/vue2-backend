package vn.mk.eid.common.icao;

import lombok.Data;
import org.jmrtd.lds.icao.DG11File;

@Data
public class ReadInfoResponse {
    String docNumber;
    String name;
    String dateOfBirth;
    String validTo;
    String dateOfIssuance;
    String gender;
    String faceImage;
    DG11File dg11;
    VNIDDG13File dg13;
    private EidCardValidationResult validationResult;
}
