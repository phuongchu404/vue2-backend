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
import vn.mk.eid.web.dto.request.report.DetaineeReportStatus;
import vn.mk.eid.web.dto.request.staff.QueryStaffRequest;
import vn.mk.eid.web.dto.response.StaffResponse;
import vn.mk.eid.web.dto.response.report.StaffReportByDepartment;
import vn.mk.eid.web.repository.StaffRepositoryCustom;
import vn.mk.eid.web.utils.StringUtil;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class StaffRepositoryImpl implements StaffRepositoryCustom {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Page<StaffResponse> getWithPaging(QueryStaffRequest request, Pageable pageable) {
        List<StaffResponse> responses = new ArrayList<>();
        Map<String, Object> params = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder(" from staff s ")
            .append(" left join ethnicities e on s.ethnicity_id = e.id ")
            .append(" left join religions r on s.religion_id = r.id ")
            .append(" left join departments d on s.department_id = d.id ")
            .append(" left join positions p on s.position_id = p.id ")
            .append(" left join wards pw on pw.code = s.permanent_ward_id ")
                .append(" LEFT JOIN provinces pp ON pp.code = pw.province_code ")
            .append(" left join wards tw on tw.code = s.temporary_ward_id ")
                .append(" LEFT JOIN provinces tp ON tp.code = tw.province_code ")
            .append(" left join education_levels el on s.education_level_id = el.id ")
                .append(" left join detention_centers dc on s.detention_center_id = dc.id ")
            .append(" WHERE 1 = 1 and s.is_active = true ");

        // staffCode
        if (StringUtil.isNotBlank(request.getStaffCode())) {
            sql.append(" and s.staff_code like :staffCode ");
            params.put("staffCode", Common.getValueSelectLike(request.getStaffCode()));
        }
        // fullName
        if (StringUtil.isNotBlank(request.getFullName())) {
            sql.append(" and s.full_name like :fullName ");
            params.put("fullName", Common.getValueSelectLike(request.getFullName()));
        }
        // rank
        if (StringUtil.isNotBlank(request.getRank())) {
            sql.append(" and s.rank like :rank ");
            params.put("rank", Common.getValueSelectLike(request.getRank()));
        }
        // status
        if (StringUtil.isNotBlank(request.getStatus())) {
            sql.append(" and s.status = :status ");
            params.put("status", request.getStatus());
        }

        String sqlText = Common.replaceWhere(sql.toString());
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(s.id) " + sqlText, params, Long.class);
        assert count != null;
        if (count.intValue() > 0) {
            String select = "SELECT s.id, " +
                    " s.staff_code     staffCode, " +
                    " s.profile_number profileNumber, " +
                    " s.full_name      fullName, " +
                    " s.gender, " +
                    " s.date_of_birth  dateOfBirth, " +
                    " s.place_of_birth placeOfBirth, " +
                    " s.id_number      idNumber, " +
                    " s.id_issue_date      idIssueDate, " +
                    " s.id_issue_place      idIssuePlace, " +
                    " e.name           ethnicityName, " +
                    " r.name           religionName, " +
                    " s.permanent_address           permanentAddress, " +
                    " pp.full_name           permanentProvinceFullName, " +
                    " pw.full_name           permanentWardFullName, " +
                    " s.temporary_address            temporaryAddress, " +
                    " tp.full_name           temporaryProvinceFullName, " +
                    " tw.full_name           temporaryWardFullName, " +
                    " s.phone, " +
                    " s.email, " +
                    " s.emergency_contact   emergencyContact, " +
                    " s.emergency_phone emergencyPhone, " +
                    " dc.name   detentionCenterName, " +
                    " d.name           departmentName, " +
                    " p.name           positionName, " +
                    " s.rank, " +
                    " el.name          educationLevelName, " +
                    " s.status, " +
                    " s.is_active      isActive, " +
                    " s.created_at     createdAt, " +
                    " s.updated_at     updatedAt ";

            Pair<String, Map<String, Object>> data = Common.setParamWithPageable(select + sqlText, params, pageable);
            responses = jdbcTemplate.query(
                    data.getLeft(),
                    data.getRight(),
                    new BeanPropertyRowMapper<>(StaffResponse.class)
            );
        }

        return new PageImpl<>(responses, pageable, count);
    }

    @Override
    public List<StaffReportByDepartment> getReportByDepartment(DetaineeReportStatus request) {
        Map<String, Object> params = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder(" from staff s ");
        sql.append(" right join departments d on s.department_id = d.id AND s.is_active = true ");

        if (request.getDetentionCenterId() != null) {
            sql.append(" AND s.detention_center_id = :detentionCenterId ");
            params.put("detentionCenterId", request.getDetentionCenterId());
        }

        sql.append(" join detention_centers dc on d.detention_center_id = dc.id ");
        sql.append(" group by d.name, dc.name ");
        String select = "select " +
                " d.name departmentName, " +
                " dc.name detentionCenterName, " +
                " count(s.id) count, " +
                " count(CASE WHEN status = 'ACTIVE' THEN s.id END) active " +
                sql;

        return jdbcTemplate.query(
                select,
                params,
                new BeanPropertyRowMapper<>(StaffReportByDepartment.class)
        );
    }
}
