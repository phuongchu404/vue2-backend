package vn.mk.eid.common.moc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MocRequest {
    private String cardReader;
    private String samReader;
    private byte[] image;
    //MoC type: 1: face, 2: left finger, 3: right finger
    private int mocType;
    private byte[] serialNumber;
    private String template;
    private int dataType = 0; //0-image, 1 - template
}
