package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.PositionEntity;
import vn.mk.eid.web.dto.request.QueryPositionRequest;
import vn.mk.eid.web.utils.StringUtil;

public class PositionSpecification {
    public static Specification<PositionEntity> getPositionSpecification(QueryPositionRequest request) {
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
