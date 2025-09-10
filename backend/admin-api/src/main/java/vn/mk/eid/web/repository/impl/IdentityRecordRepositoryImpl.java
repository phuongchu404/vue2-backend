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
import vn.mk.eid.web.dto.request.identity_record.QueryIdentityRecordRequest;
import vn.mk.eid.web.dto.response.IdentityRecordResponse;
import vn.mk.eid.web.repository.IdentityRecordRepositoryCustom;
import vn.mk.eid.web.utils.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class IdentityRecordRepositoryImpl implements IdentityRecordRepositoryCustom {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Page<IdentityRecordResponse> getWithPaging(QueryIdentityRecordRequest request, Pageable pageable) {
        List<IdentityRecordResponse> responses = new ArrayList<>();
        Map<String, Object> params = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder(" from identity_record ir ")
                .append(" join detainees d on ir.detainee_id = d.id ")
                .append(" WHERE 1 = 1 ");

        // detaineeCode
        if (StringUtil.isNotBlank(request.getDetaineeCode())) {
            sql.append(" and d.detainee_code like :detaineeCode ");
            params.put("detaineeCode", Common.getValueSelectLike(request.getDetaineeCode()));
        }
        // detaineeName
        if (StringUtil.isNotBlank(request.getDetaineeName())) {
            sql.append(" and d.full_name like :detaineeName ");
            params.put("detaineeName", Common.getValueSelectLike(request.getDetaineeName()));
        }
        // arrestUnit
        if (request.getArrestUnit() != null) {
            sql.append(" and ir.arrest_unit = :arrestUnit ");
            params.put("arrestUnit", request.getArrestUnit());
        }

        String sqlText = Common.replaceWhere(sql.toString());
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(ir.id) " + sqlText, params, Long.class);
        assert count != null;
        if (count.intValue() > 0) {
            String select = "select ir.id, " +
                    " ir.detainee_id detaineeId, " +
                    " d.full_name detaineeName, " +
                    " d.detainee_code detaineeCode, " +
                    " ir.created_place createdPlace, " +
                    " ir.reason_note reasonNote, " +
                    " ir.arrest_date arrestDate, " +
                    " ir.arrest_unit arrestUnit, " +
                    " ir.fp_classification fpClassification, " +
                    " ir.dp, " +
                    " ir.tw, " +
                    " ir.ak_file_no akFileNo, " +
                    " ir.notes, " +
                    " ir.created_at createdAt, " +
                    " ir.updated_at updatedAt ";

            Pair<String, Map<String, Object>> data = Common.setParamWithPageable(select + sqlText, params, pageable);
            responses = jdbcTemplate.query(
                    data.getLeft(),
                    data.getRight(),
                    new BeanPropertyRowMapper<>(IdentityRecordResponse.class)
            );
        }

        return new PageImpl<>(responses, pageable, count);
    }
}
