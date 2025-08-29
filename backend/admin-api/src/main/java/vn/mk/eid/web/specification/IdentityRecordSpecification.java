package vn.mk.eid.web.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.mk.eid.common.constant.Constants;
import vn.mk.eid.common.dao.entity.IdentityRecordEntity;
import vn.mk.eid.common.util.DateUtils;
import vn.mk.eid.web.dto.request.QueryIdentityRecordRequest;
import vn.mk.eid.web.utils.StringUtil;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IdentityRecordSpecification {
    public static Specification<IdentityRecordEntity> getIdentityRecordSpecification(QueryIdentityRecordRequest filter) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // arrestUnit
            if (StringUtil.isNotBlank(filter.getArrestUnit())) {
                predicates.add(builder.like(
                        builder.lower(root.get("arrestUnit")),
                        "%" + filter.getArrestUnit().toLowerCase() + "%"
                ));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
