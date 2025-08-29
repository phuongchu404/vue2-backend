package vn.mk.eid.common.card;

import lombok.Data;

@Data
public class TransmitSamRequest {
    String sessionId;
    Boolean lastRequest;
    String cmdApdu;
}
