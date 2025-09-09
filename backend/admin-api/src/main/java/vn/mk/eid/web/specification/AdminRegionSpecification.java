package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.AdministrativeRegionEntity;
import vn.mk.eid.web.dto.request.administrative_region.QueryAdministrativeRegionRequest;
import vn.mk.eid.web.utils.StringUtil;

public class AdminRegionSpecification {
    public static Specification<AdministrativeRegionEntity> filterByKeyword(QueryAdministrativeRegionRequest filter) {
        return ((root, query, builder) -> {
            if (StringUtil.isBlank(filter.getKeyword())) {
                return builder.conjunction();
            }

            String keywordPattern = "%" + filter.getKeyword().toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("name")), keywordPattern),
                    builder.like(builder.lower(root.get("nameEn")), keywordPattern)
            );
        });
    }
}
