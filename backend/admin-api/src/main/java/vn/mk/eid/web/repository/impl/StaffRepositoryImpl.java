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
import vn.mk.eid.web.dto.request.QueryStaffRequest;
import vn.mk.eid.web.dto.response.StaffResponse;
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
            .append(" left join education_levels el on s.education_level_id = el.id ")
            .append(" WHERE 1 = 1 ");

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
                    " s.gender genderText, " +
                    " s.date_of_birth  dateOfBirth, " +
                    " s.place_of_birth placeOfBirth, " +
                    " s.id_number      idNumber, " +
                    " e.name           ethnicityName, " +
                    " r.name           religionName, " +
                    " s.phone, " +
                    " s.email, " +
                    " d.name           departmentName, " +
                    " p.name           positionName, " +
                    " s.rank, " +
                    " el.name          educationLevelName, " +
                    " status, " +
                    " s.is_active      isActive, " +
                    " s.created_at     createdAt, " +
                    " s.updated_at     updatedAt ";

            Pair<String, Map<String, Object>> data = Common.queryWithPageable(select + sqlText, params, pageable);
            responses = jdbcTemplate.query(
                    data.getLeft(),
                    data.getRight(),
                    new BeanPropertyRowMapper<>(StaffResponse.class)
            );
        }

        return new PageImpl<>(responses, pageable, count);
    }
}
