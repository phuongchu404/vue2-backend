package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.WardEntity;
import vn.mk.eid.web.dto.request.QueryWardRequest;
import vn.mk.eid.web.utils.StringUtil;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class WardSpecification {
    public static Specification<WardEntity> getWardSpecification(QueryWardRequest filter) {
        return Specification
                .where(filterByKeyword(filter))
                .and((root, query, builder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    // provinceCode
                    if (StringUtil.isNotBlank(filter.getProvinceCode())) {
                        predicates.add(builder.equal(root.get("provinceCode"), filter.getProvinceCode()));
                    }

                    // administrativeUnitId
                    if (filter.getAdministrativeUnitId() != null) {
                        predicates.add(builder.equal(root.get("administrativeUnitId"), filter.getAdministrativeUnitId()));
                    }

                    return builder.and(predicates.toArray(new Predicate[0]));
                });
    }

    public static Specification<WardEntity> filterByKeyword(QueryWardRequest filter) {
        return ((root, query, builder) -> {
            if (StringUtil.isBlank(filter.getKeyword())) {
                return builder.conjunction();
            }

            String keywordPattern = "%" + filter.getKeyword().toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("name")), keywordPattern),
                    builder.like(builder.lower(root.get("nameEn")), keywordPattern),
                    builder.like(builder.lower(root.get("code")), keywordPattern)
            );
        });
    }
}
