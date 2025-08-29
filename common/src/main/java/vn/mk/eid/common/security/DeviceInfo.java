package vn.mk.eid.common.security;

import lombok.Data;

@Data
public class DeviceInfo {
    String deviceID = "";
    String publicKey = "";
    String privateKey = "";
    String secretKey = "";
    String serverPubKey = "";
}
