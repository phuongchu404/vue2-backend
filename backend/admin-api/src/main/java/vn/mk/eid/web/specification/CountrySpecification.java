package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.dao.entity.CountryEntity;
import vn.mk.eid.web.dto.request.QueryWardRequest;
import vn.mk.eid.web.utils.StringUtil;

public class CountrySpecification {
    public static Specification<CountryEntity> getCountrySpecification(QueryWardRequest filter) {
        return ((root, query, builder) -> {
            if (StringUtil.isBlank(filter.getKeyword())) {
                return builder.conjunction();
            }

            String keywordPattern = "%" + filter.getKeyword().toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("countryName")), keywordPattern),
                    builder.like(builder.lower(root.get("alpha2Code")), keywordPattern),
                    builder.like(builder.lower(root.get("alpha3Code")), keywordPattern)
            );
        });
    }
}
