package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
/* * @author mk
 * @date 06-Aug-2025
 */
// bang danh sach trinh do hoc van
@Getter
@Setter
@Entity
@Table(name = "education_levels")
public class EducationLevelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @javax.validation.constraints.Size(max = 100)
    @javax.validation.constraints.NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @javax.validation.constraints.Size(max = 20)
    @javax.validation.constraints.NotNull
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "level_order")
    private Integer levelOrder;

}