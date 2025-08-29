package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author mk
 * @date 06-Aug-2025
 */
// bang danh sach ton giao
@Getter
@Setter
@Entity
@Table(name = "religions")
public class ReligionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "code", nullable = false, length = 10)
    private String code;

}