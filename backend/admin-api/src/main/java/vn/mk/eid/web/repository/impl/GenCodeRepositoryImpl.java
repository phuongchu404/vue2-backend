package vn.mk.eid.web.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.mk.eid.web.repository.GenCodeRepository;

import java.util.HashMap;

@Repository
@RequiredArgsConstructor
public class GenCodeRepositoryImpl implements GenCodeRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Long getValueByTableName(String tableName) {
        return jdbcTemplate.queryForObject(
                "select count(*) from " + tableName,
                new HashMap<>(),
                Long.class
        );
    }
}
