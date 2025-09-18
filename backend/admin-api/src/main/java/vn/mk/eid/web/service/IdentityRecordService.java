package vn.mk.eid.web.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.identity_record.IdentityRecordCreateRequest;
import vn.mk.eid.web.dto.request.identity_record.IdentityRecordUpdateRequest;
import vn.mk.eid.web.dto.request.identity_record.QueryIdentityRecordRequest;


public interface IdentityRecordService {

    ServiceResult createIdentityRecord(IdentityRecordCreateRequest request, MultipartFile front, MultipartFile leftProfile, MultipartFile rightProfile);

    ServiceResult updateIdentityRecord(Long id, IdentityRecordUpdateRequest request, MultipartFile front, MultipartFile leftProfile, MultipartFile rightProfile);

    ServiceResult getIdentityRecord(Long id);

    ServiceResult getIdentityRecordByDetaineeId(Long detaineeId);

    ServiceResult getIdentityRecordWithPaging(QueryIdentityRecordRequest request, Pageable pageable);

    ServiceResult deleteIdentityRecord(Long id);

    ServiceResult countIdentityRecords();
}
