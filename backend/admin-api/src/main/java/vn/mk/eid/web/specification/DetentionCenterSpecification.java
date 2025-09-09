package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.DetentionCenterEntity;
import vn.mk.eid.web.dto.request.detention_center.QueryDetentionCenterRequest;
import vn.mk.eid.web.utils.StringUtil;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class DetentionCenterSpecification {
    public static Specification<DetentionCenterEntity> getDetentioncneterSpecification(QueryDetentionCenterRequest filter) {
        return Specification
                .where(filterByKeyword(filter))
                .and(((root, query, builder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    // provinceId
                    if (filter.getProvinceId() != null) {
                        predicates.add(builder.equal(root.get("provinceId"), filter.getProvinceId()));
                    }

                    // wardId
                    if (filter.getWardId() != null) {
                        predicates.add(builder.equal(root.get("wardId"), filter.getWardId()));
                    }

                    return builder.and(predicates.toArray(new Predicate[0]));
                }));
    }

    public static Specification<DetentionCenterEntity> filterByKeyword(QueryDetentionCenterRequest filter) {
        return ((root, query, builder) -> {
            if (StringUtil.isBlank(filter.getKeyword())) {
                return builder.conjunction();
            }

            String keywordPattern = "%" + filter.getKeyword().toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("name")), keywordPattern),
                    builder.like(builder.lower(root.get("code")), keywordPattern)
            );
        });
    }
}
