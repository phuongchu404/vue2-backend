package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author mk
 * @date 06-Aug-2025
 */
// bang khu vuc dia ly
@Getter
@Setter
@Entity
@Table(name = "administrative_regions")
public class AdministrativeRegionEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Size(max = 255)
    @Column(name = "code_name")
    private String codeName;

    @Size(max = 255)
    @Column(name = "code_name_en")
    private String codeNameEn;

}