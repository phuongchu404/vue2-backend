package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * @author mk
 * @date 06-Aug-2025
 */
// bang danh sach cac don vi hanh chinh
@Getter
@Setter
@Entity
@Table(name = "administrative_units")
public class AdministrativeUnitEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 255)
    @Column(name = "full_name_en")
    private String fullNameEn;

    @Size(max = 255)
    @Column(name = "short_name")
    private String shortName;

    @Size(max = 255)
    @Column(name = "short_name_en")
    private String shortNameEn;

    @Size(max = 255)
    @Column(name = "code_name")
    private String codeName;

    @Size(max = 255)
    @Column(name = "code_name_en")
    private String codeNameEn;

}