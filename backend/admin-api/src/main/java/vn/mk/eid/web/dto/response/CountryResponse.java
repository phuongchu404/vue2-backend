package vn.mk.eid.web.dto.response;

import lombok.Data;

@Data
public class CountryResponse {
    private Long id;
    private String alpha2Code;
    private String alpha3Code;
    private String name;
    private String numericCode;
}
