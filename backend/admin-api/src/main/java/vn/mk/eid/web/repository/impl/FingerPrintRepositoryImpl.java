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
import vn.mk.eid.web.dto.request.QueryFingerPrintRequest;
import vn.mk.eid.web.dto.response.FingerprintCardResponse;
import vn.mk.eid.web.repository.FingerPrintRepositoryCustom;
import vn.mk.eid.web.utils.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FingerPrintRepositoryImpl implements FingerPrintRepositoryCustom {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Page<FingerprintCardResponse> getWithPaging(Pageable pageable, QueryFingerPrintRequest request) {
        List<FingerprintCardResponse> responses = new ArrayList<>();
        Map<String, Object> params = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder(" from fingerprint_card fr ")
                .append(" join detainees d on fr.person_id = d.id ")
                .append(" WHERE 1 = 1 ");

        // detentionCenterId
        if (request.getDetentionCenterId() != null) {
            sql.append(" and d.detention_center_id = :detentionCenterId ");
            params.put("detentionCenterId", request.getDetentionCenterId());
        }
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

        String sqlText = Common.replaceWhere(sql.toString());
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(fr.id) " + sqlText, params, Long.class);
        assert count != null;
        if (count.intValue() > 0) {
            String select = "select " +
                    " fr.id, " +
                    " person_id detaineeId, " +
                    " d.full_name detaineeName, " +
                    " d.detainee_code detaineeCode, " +
                    " created_place createdPlace, " +
                    " dp dp, " +
                    " tw tw, " +
                    " fp_formula fpFormula, " +
                    " fr.created_at createdAt, " +
                    " reason_note reasonNote ";
            Pair<String, Map<String, Object>> data = Common.setParamWithPageable(select + sqlText, params, pageable);
            responses = jdbcTemplate.query(
                    data.getLeft(),
                    data.getRight(),
                    new BeanPropertyRowMapper<>(FingerprintCardResponse.class)
            );
        }

        return new PageImpl<>(responses, pageable, count);
    }
}
