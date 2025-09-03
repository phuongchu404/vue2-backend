package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author mk
 * @date 06-Aug-2025
 */
// bang danh sach phuong xa
@Getter
@Setter
@Entity
@Table(name = "wards")
public class WardEntity {
    @Id
    @Size(max = 20)
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "name_en")
    private String nameEn;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 255)
    @Column(name = "full_name_en")
    private String fullNameEn;

    @Size(max = 255)
    @Column(name = "code_name")
    private String codeName;

    //    @Column(name = "province_code")
//    private String provinceCode;
//
//    @Column(name = "administrative_unit_id")
//    private Integer administrativeUnitId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "province_code", referencedColumnName = "code")
    private ProvinceEntity province;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "administrative_unit_id", referencedColumnName = "id")
    private AdministrativeUnitEntity administrativeUnit;
}