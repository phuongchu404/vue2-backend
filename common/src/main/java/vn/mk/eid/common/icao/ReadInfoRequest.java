package vn.mk.eid.common.icao;

import lombok.Data;

@Data
public class ReadInfoRequest extends BaseCardRequest {
    String docNumber;
    String dateOfBirth;
    String validTo;

    private String key;
    private String keyReference;

    private String reader;
}
