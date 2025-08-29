package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.EthnicityEntity;
import vn.mk.eid.web.utils.StringUtil;

public class EthnicitySpecification {
    public static Specification<EthnicityEntity> getEthnicityEntitySpecification(String keyword) {
        return ((root, query, builder) -> {
            if (StringUtil.isBlank(keyword)) {
                return builder.conjunction();
            }

            String keywordPattern = "%" + keyword.toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("name")), keywordPattern),
                    builder.like(builder.lower(root.get("code")), keywordPattern)
            );
        });
    }
}
