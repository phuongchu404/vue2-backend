package vn.mk.eid;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import vn.mk.eid.common.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Common {
    public static Pair<String, Map<String, Object>> queryWithPageable(
            String sql,
            Map<String, Object> params,
            Pageable pageable
    ) {
        StringBuilder sqlBuilder = new StringBuilder(sql);

        // Thêm sort nếu có
        if (pageable.getSort().isSorted()) {
            sqlBuilder.append(" ORDER BY ");
            String orderBy = pageable.getSort().stream()
                    .map(order -> order.getProperty() + " " + order.getDirection().name())
                    .collect(Collectors.joining(", "));
            sqlBuilder.append(orderBy);
        }

        // Thêm limit và offset
        sqlBuilder.append(" LIMIT :limit OFFSET :offset ");

        Map<String, Object> pagingParams = new HashMap<>(params);
        pagingParams.put("limit", pageable.getPageSize());
        pagingParams.put("offset", pageable.getOffset());

        return Pair.of(sqlBuilder.toString(), pagingParams);
    }

    public static String replaceWhere(String sql) {
        if (StringUtil.isBlank(sql)) {
            return sql;
        }
        // [where 1 = 1 and] --> [where]
        sql = sql.replaceAll("(?i)\\bWHERE\\s+1\\s+=\\s+1\\s+AND\\b", " WHERE ");
        // remove [where 1 = 1]
        sql = sql.replaceAll("(?i)\\bWHERE\\s+1\\s+=\\s+1\\b", "");
        return sql.trim();
    }

    public static String getValueSelectLike(String value) {
        return "%" + value + "%";
    }
}
