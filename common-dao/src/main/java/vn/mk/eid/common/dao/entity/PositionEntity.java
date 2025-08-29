package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * @author mk
 * @date 06-Aug-2025
 */
// Bang chuc vu nhan vien trong trai giam
@Getter
@Setter
@Entity
@Table(name = "positions")
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "level")
    private Integer level;

    @Column(name = "description")
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "applies_to", length = 20)
    private String appliesTo;

}