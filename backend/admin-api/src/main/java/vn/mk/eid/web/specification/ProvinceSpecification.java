package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.ProvinceEntity;
import vn.mk.eid.web.dto.request.QueryProvinceRequest;
import vn.mk.eid.web.utils.StringUtil;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProvinceSpecification {
    public static Specification<ProvinceEntity> getProvinceSpecification(QueryProvinceRequest filter) {
        return Specification
                .where(filterByKeyword(filter))
                .and(((root, query, builder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    // administrativeUnitId
                    if (filter.getAdministrativeUnitId() != null) {
                        predicates.add(builder.equal(root.get("administrativeUnitId"), filter.getAdministrativeUnitId()));
                    }

                    return builder.and(predicates.toArray(new Predicate[0]));
                }));
    }

    public static Specification<ProvinceEntity> filterByKeyword(QueryProvinceRequest filter) {
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
