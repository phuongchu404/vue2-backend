package vn.mk.eid.common.entities.eid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequestVerificationKeys {
    String serverPrivateKey;
    String userPublicKey;
    String deviceSecretKey;
    byte[] sessionKeyAES;
}
