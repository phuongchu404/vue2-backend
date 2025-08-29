package vn.mk.eid.common.moc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MocRequest2 {
    private String cardReader;
    private String samReader;
    private byte[] faceImage;
    private byte[] lifImage;
    private byte[] rifImage;
}
