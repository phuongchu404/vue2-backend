package vn.mk.eid.common.icao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CVCert {
    String authorityRef;
    String body;
    String signature;
    String cert;
    //    String signature;
    String type;
}
