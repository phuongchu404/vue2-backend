package vn.mk.eid.common.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "department_statistics_fact")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentStatisticsFactEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "staff_count")
    private Integer staffCount = 0;

    @Column(name = "active_staff_count")
    private Integer activeStaffCount = 0;

    @Column(name = "detainees_assigned")
    private Integer detaineesAssigned = 0;

    @Column(name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    private DepartmentEntity department;
}
