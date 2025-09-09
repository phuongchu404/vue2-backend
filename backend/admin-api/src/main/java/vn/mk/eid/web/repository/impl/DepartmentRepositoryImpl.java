package vn.mk.eid.web.repository.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.mk.eid.Common;
import vn.mk.eid.web.dto.request.department.QueryDepartmentRequest;
import vn.mk.eid.web.dto.response.DepartmentResponse;
import vn.mk.eid.web.repository.DepartmentRepositoryCustom;
import vn.mk.eid.web.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepositoryCustom {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Page<DepartmentResponse> getWithPaging(QueryDepartmentRequest request, Pageable pageable) {
        List<DepartmentResponse> responses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder(" from departments d ")
                .append(" join detention_centers dc on d.detention_center_id = dc.id ")
                .append(" WHERE 1 = 1 ");

        // detentionCenterId
        if (request.getDetentionCenterId() != null) {
            sql.append(" and d.detention_center_id = :detentionCenterId ");
            params.put("detentionCenterId", request.getDetentionCenterId());
        }

        // keyword
        if (StringUtil.isNotBlank(request.getKeyword())) {
            sql.append(" and (d.name like :keyword or d.code like :keyword ) ");
            params.put("keyword", Common.getValueSelectLike(request.getKeyword()));
        }

        String sqlText = Common.replaceWhere(sql.toString());
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(d.id) " + sqlText, params, Long.class);
        assert count != null;
        if (count.intValue() > 0) {
            String select = "select d.id, " +
                    " d.name, " +
                    " d.code, " +
                    " d.detention_center_id detentionCenterId, " +
                    " dc.code detentionCenterCode, " +
                    " dc.name detentionCenterName, " +
                    " d.description, " +
                    " d.is_active isActive ";

            Pair<String, Map<String, Object>> data;
            if (pageable == null) {
                data = Common.setParam(select + sqlText, params);
            } else {
                data = Common.setParamWithPageable(select + sqlText, params, pageable);
            }

            responses = jdbcTemplate.query(
                    data.getLeft(),
                    data.getRight(),
                    new BeanPropertyRowMapper<>(DepartmentResponse.class)
            );
        }

        return pageable == null ? new PageImpl<>(responses) : new PageImpl<>(responses, pageable, count);
    }
}
