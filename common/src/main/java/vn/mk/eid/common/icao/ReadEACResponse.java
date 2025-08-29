package vn.mk.eid.common.icao;

import lombok.Data;

import java.util.List;

@Data
public class ReadEACResponse extends ReadInfoResponse {
    List<FingerImage> fingerImages;
    String transactionId;
}
