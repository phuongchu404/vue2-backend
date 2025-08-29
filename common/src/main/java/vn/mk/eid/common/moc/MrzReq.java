package vn.mk.eid.common.moc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MrzReq {
    String random;
    String mrzenc;
}
