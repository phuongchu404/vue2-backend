package vn.mk.eid.common.icao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EidCardValidationResult {
    private boolean aa = false;
    private boolean pa = false;
//    private boolean ta = false;
    private boolean ca = false;
}
