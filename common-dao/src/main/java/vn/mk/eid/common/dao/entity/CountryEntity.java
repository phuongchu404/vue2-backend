package vn.mk.eid.common.dao.entity;


import lombok.Data;

import javax.persistence.*;

/**
 * @author mk
 * @date 06-Aug-2025
 */
@Data
@Entity
@Table(name = "country")
public class CountryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_name", length = 100)
    private String countryName;

    @Column(name = "alpha_2_code", length = 2)
    private String alpha2Code;

    @Column(name = "alpha_3_code", length = 3)
    private String alpha3Code;

    @Column(name = "numeric _code", length = 5)
    private String numericCode;
}
