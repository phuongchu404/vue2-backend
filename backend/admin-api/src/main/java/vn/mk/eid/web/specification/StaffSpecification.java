package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.StaffEntity;
import vn.mk.eid.web.dto.request.QueryStaffRequest;
import vn.mk.eid.web.utils.StringUtil;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class StaffSpecification {
    public static Specification<StaffEntity> getStaffSpecification(QueryStaffRequest filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // staffCode
            if (StringUtil.isNotBlank(filter.getStaffCode())) {
                predicates.add(builder.like(
                        builder.lower(root.get("staffCode")),
                        "%" + filter.getStaffCode().toLowerCase() + "%"
                ));
            }

            // fullName
            if (StringUtil.isNotBlank(filter.getFullName())) {
                predicates.add(builder.like(
                        builder.lower(root.get("fullName")),
                        "%" + filter.getFullName().toLowerCase() + "%"
                ));
            }

            // rank
            if (StringUtil.isNotBlank(filter.getRank())) {
                predicates.add(builder.equal(root.get("rank"), filter.getRank()));
            }

            // status
            if (StringUtil.isNotBlank(filter.getStatus())) {
                predicates.add(builder.equal(root.get("status"), filter.getStatus()));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
