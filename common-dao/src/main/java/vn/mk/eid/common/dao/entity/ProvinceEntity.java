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
// bang danh sach tinh/thanh
@Getter
@Setter
@Entity
@Table(name = "provinces")
public class ProvinceEntity {
    @Id
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
    @NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Size(max = 255)
    @Column(name = "full_name_en")
    private String fullNameEn;

    @Size(max = 255)
    @Column(name = "code_name")
    private String codeName;

    //    @Column(name = "administrative_unit_id")
//    private Integer administrativeUnitId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "administrative_unit_id", referencedColumnName = "id")
    private AdministrativeUnitEntity administrativeUnit;
}