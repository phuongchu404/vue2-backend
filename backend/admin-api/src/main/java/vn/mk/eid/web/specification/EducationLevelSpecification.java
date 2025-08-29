package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.EducationLevelEntity;
import vn.mk.eid.web.dto.request.QueryEducationLevelRequest;
import vn.mk.eid.web.utils.StringUtil;

public class EducationLevelSpecification {
    public static Specification<EducationLevelEntity> getEducationLevelSpecification(QueryEducationLevelRequest request) {
        return ((root, query, builder) -> {
            if (StringUtil.isBlank(request.getKeyword())) {
                return builder.conjunction();
            }

            String keywordPattern = "%" + request.getKeyword().toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("name")), keywordPattern),
                    builder.like(builder.lower(root.get("code")), keywordPattern)
            );
        });
    }
}
