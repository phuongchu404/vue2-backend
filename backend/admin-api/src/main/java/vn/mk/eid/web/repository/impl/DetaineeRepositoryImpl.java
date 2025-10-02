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
import vn.mk.eid.web.dto.request.detainee.QueryDetaineeRequest;
import vn.mk.eid.web.dto.request.report.DetaineeReportStatus;
import vn.mk.eid.web.dto.response.DetaineeResponse;
import vn.mk.eid.web.dto.response.report.DetaineeReportByMonth;
import vn.mk.eid.web.dto.response.report.DetaineeReportByStatus;
import vn.mk.eid.web.repository.DetaineeRepositoryCustom;
import vn.mk.eid.web.utils.StringUtil;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class DetaineeRepositoryImpl implements DetaineeRepositoryCustom {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Page<DetaineeResponse> getWithPaging(QueryDetaineeRequest request, Pageable pageable) {
        List<DetaineeResponse> responses = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder(" from detainees d ")
                .append(" join detention_centers dc on d.detention_center_id = dc.id ")
                .append(" left join ethnicities e on d.ethnicity_id = e.id ")
                .append(" left join religions r on d.religion_id = r.id ")
                .append(" left join country c on d.nationality_id = c.id ")
                .append(" left join wards pw on pw.code = d.permanent_ward_id ")
                .append(" LEFT JOIN provinces pp ON pp.code = pw.province_code ")
                .append(" left join wards tw on tw.code = d.temporary_ward_id ")
                .append(" LEFT JOIN provinces tp ON tp.code = tw.province_code ")
                .append(" left join wards cw on cw.code = d.current_ward_id ")
                .append(" LEFT JOIN provinces cp ON cp.code = cw.province_code ")
                .append(" WHERE 1 = 1 ");

        // detentionCenterId
        if (request.getDetentionCenterId() != null) {
            sql.append(" AND d.detention_center_id = :detentionCenterId ");
            params.put("detentionCenterId", request.getDetentionCenterId());
        }

        // status
        if (StringUtil.isNotBlank(request.getStatus())) {
            sql.append(" AND d.status = :status ");
            params.put("status", request.getStatus());
        }

        if(StringUtil.isNotBlank(request.getDetaineeCode())){
            sql.append(" AND LOWER(d.detainee_code) like LOWER(:detaineeCode) ");
            params.put("detaineeCode", Common.getValueSelectLike(request.getDetaineeCode()));
        }
        if(StringUtil.isNotBlank(request.getIdNumber())){
            sql.append(" AND d.id_number = :idNumber ");
            params.put("idNumber", request.getIdNumber());
        }
        if(StringUtil.isNotBlank(request.getFullName())){
            sql.append(" AND LOWER(d.full_name) LIKE LOWER(:fullName) ");
            params.put("fullName", Common.getValueSelectLike(request.getFullName()));
        }

        // keyword
//        if (StringUtil.isNotBlank(request.getKeyword())) {
//            sql.append(" and (d.name like :keyword or d.code like :keyword or d.id_number like :keyword ) ");
//            params.put("keyword", Common.getValueSelectLike(request.getKeyword()));
//        }

        String sqlText = Common.replaceWhere(sql.toString());
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(d.id) " + sqlText, params, Long.class);
        assert count != null;
        if (count.intValue() > 0) {
            String select = "select d.id, " +
                    " d.detainee_code detaineeCode, " +
                    " d.profile_number profileNumber, " +
                    " d.full_name fullName, " +
                    " d.alias_name aliasName, " +
                    " d.gender gender, " +
                    " d.date_of_birth dateOfBirth, " +
                    " d.place_of_birth placeOfBirth, " +
                    " d.id_issue_date idIssueDate, " +
                    " d.id_issue_place idIssuePlace, " +
                    " d.id_number idNumber, " +
                    " d.nationality_id nationalityId, " +
                    " d.ethnicity_id ethnicityId, " +
                    " d.religion_id religionId, " +
                    " c.country_name nationalityName, " +
                    " e.name ethnicityName, " +
                    " r.name religionName, " +
                    " d.permanent_address           permanentAddress, " +
                    " pp.full_name           permanentProvinceFullName, " +
                    " pw.full_name           permanentWardFullName, " +
                    " d.temporary_address            temporaryAddress, " +
                    " tp.full_name           temporaryProvinceFullName, " +
                    " tw.full_name           temporaryWardFullName, " +
                    " d.current_address currentAddress, " +
                    " cp.full_name           currentProvinceFullName, " +
                    " cw.full_name           currentWardFullName, " +
                    " d.occupation occupation, " +
                    " d.father_name fatherName, " +
                    " d.mother_name motherName, " +
                    " d.spouse_name spouseName, " +
                    " d.detention_date detentionDate, " +
                    " d.expected_release_date expectedReleaseDate, " +
                    " d.actual_release_date actualReleaseDate, " +
                    " d.case_number caseNumber, " +
                    " d.charges, " +
                    " d.sentence_duration sentenceDuration, " +
                    " d.court_name courtName, " +
                    " d.detention_center_id detentionCenterId, " +
                    " dc.code detentionCenterCode, " +
                    " dc.name detentionCenterName, " +
                    " d.cell_number cellNumber, " +
                    " d.status, " +
                    " d.notes, " +
                    " d.created_at createdAt, " +
                    " d.updated_at updatedAt ";

            Pair<String, Map<String, Object>> data = Common.setParamWithPageable(select + sqlText, params, pageable);
            responses = jdbcTemplate.query(
                    data.getLeft(),
                    data.getRight(),
                    new BeanPropertyRowMapper<>(DetaineeResponse.class)
            );
        }

        return new PageImpl<>(responses, pageable, count);
    }

    @Override
    public List<DetaineeReportByStatus> getDetaineeReportByStatus(DetaineeReportStatus request) {
        List<DetaineeReportByStatus> responses;
        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ")
            .append(" detainee_id, ")
            .append(" detention_center_id, ")
            .append(" type, ")
            .append(" start_date, ")
            .append(" ROW_NUMBER() OVER (PARTITION BY detainee_id ORDER BY start_date DESC) AS rn ")
            .append(" FROM detention_history ")
            .append(" WHERE 1 = 1 ");

        if (request.getDetentionCenterId() != null) {
            sql.append(" AND detention_center_id = :detentionCenterId ");
            params.put("detentionCenterId", request.getDetentionCenterId());
        }

        if (StringUtil.isNotBlank(request.getToDate())) {
            sql.append(" AND start_date <= TO_DATE(:toDate, 'YYYY-MM-DD') ");
            params.put("toDate", request.getToDate());
        }

        StringBuilder selectSql = new StringBuilder();
        selectSql.append(" WITH latest_history AS ( ")
                .append(sql)
                .append(" ) ")
                .append(" SELECT type, COUNT(DISTINCT detainee_id) AS count ")
                .append(" FROM latest_history ")
                .append(" WHERE rn = 1 ")
                .append(" group by type ");

        responses = jdbcTemplate.query(
                selectSql.toString(),
                params,
                new BeanPropertyRowMapper<>(DetaineeReportByStatus.class)
        );

        return responses;
    }

    @Override
    public List<DetaineeReportByMonth> getDetaineeReportByMonth(DetaineeReportStatus request) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append(" FROM detention_history ");
        sql.append(" WHERE 1 = 1 ");

        if (request.getDetentionCenterId() != null) {
            sql.append(" AND detention_center_id = :detentionCenterId ");
            params.put("detentionCenterId", request.getDetentionCenterId());
        }

        if (StringUtil.isNotBlank(request.getFromDate())) {
            sql.append(" AND start_date >= TO_DATE(:fromDate, 'YYYY-MM-DD') ");
            params.put("fromDate", request.getFromDate());
        }

        if (StringUtil.isNotBlank(request.getToDate())) {
            sql.append(" AND start_date <= TO_DATE(:toDate, 'YYYY-MM-DD') ");
            params.put("toDate", request.getToDate());
        }

        sql.append(" GROUP BY TO_CHAR(start_date, 'MM/YYYY') ");
        sql.append(" ORDER BY month ");

        String selectSql = "SELECT " +
                "  TO_CHAR(start_date, 'MM/YYYY') AS month, " +
                "  COUNT(CASE WHEN type = 'INITIAL' THEN detainee_id END) AS newDetainees, " +
                "  COUNT(CASE WHEN type = 'RELEASED' THEN detainee_id END) AS released " +
                sql;

        return jdbcTemplate.query(
                selectSql,
                params,
                new BeanPropertyRowMapper<>(DetaineeReportByMonth.class)
        );
    }


}
