package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.AdministrativeUnitEntity;
import vn.mk.eid.web.dto.request.administrative_unit.QueryAdministrativeUnitRequest;
import vn.mk.eid.web.utils.StringUtil;

public class AdminUnitSpecification {
    public static Specification<AdministrativeUnitEntity> filterByKeyword(QueryAdministrativeUnitRequest filter) {
        return ((root, query, builder) -> {
            if (StringUtil.isBlank(filter.getKeyword())) {
                return builder.conjunction();
            }

            String keywordPattern = "%" + filter.getKeyword().toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("fullName")), keywordPattern),
                    builder.like(builder.lower(root.get("fullNameEn")), keywordPattern),
                    builder.like(builder.lower(root.get("shortName")), keywordPattern),
                    builder.like(builder.lower(root.get("shortNameEn")), keywordPattern)
            );
        });
    }
}
