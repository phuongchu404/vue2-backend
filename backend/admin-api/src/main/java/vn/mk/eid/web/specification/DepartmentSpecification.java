package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.DepartmentEntity;
import vn.mk.eid.web.dto.request.department.QueryDepartmentRequest;
import vn.mk.eid.web.utils.StringUtil;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class DepartmentSpecification {
    public static Specification<DepartmentEntity> getDepartmentSpecification(QueryDepartmentRequest filter) {
        return Specification
                .where(filterByKeyword(filter))
                .and(((root, query, builder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (filter.getDetentionCenterId() != null) {
                        predicates.add(builder.equal(root.get("detentionCenterId"), filter.getDetentionCenterId()));
                    }

                    return builder.and(predicates.toArray(new Predicate[0]));
                }));
    }

    public static Specification<DepartmentEntity> filterByKeyword(QueryDepartmentRequest filter) {
        return ((root, query, builder) -> {
            if (StringUtil.isBlank(filter.getKeyword())) {
                return builder.conjunction();
            }

            String keywordPattern = "%" + filter.getKeyword().toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("code")), keywordPattern),
                    builder.like(builder.lower(root.get("name")), keywordPattern)
            );
        });
    }
}
