package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.constant.Constants;
import vn.mk.eid.web.repository.GenCodeRepository;
import vn.mk.eid.web.service.RedisService;
import vn.mk.eid.web.service.SequenceService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class SequenceServiceImpl implements SequenceService {
    private final RedisService redisService;
    private final GenCodeRepository genCodeRepository;

    @Override
    public String genCode(String prefix) {
        String year = String.valueOf(LocalDate.now().getYear());

        long count;
        Object redisData = redisService.getFromHash(Constants.RedisKey.GEN_CODE, prefix);
        // redis null --> count(*) trong DB
        if (Objects.isNull(redisData)) {
            count = getValueByTableName(prefix);
            redisService.putToHash(Constants.RedisKey.GEN_CODE, prefix, count);
        } else {
            count = redisService.incrementHash(Constants.RedisKey.GEN_CODE, prefix);
        }
        return String.format("%s%s%06d", prefix, year, count);
    }

    private Long getValueByTableName(String prefix) {
        String tableName = genTableNameByPrefix(prefix);
        return genCodeRepository.getValueByTableName(tableName) + 1;
    }

    private String genTableNameByPrefix(String prefix) {
        switch (prefix) {
            case Constants.CodePrefix.STAFF_CODE:
                return Constants.TableName.STAFF;
            case Constants.CodePrefix.DETAINEE_CODE:
                return Constants.TableName.DETAINEE;
        }
        return "";
    }
}
