package vn.mk.eid.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mk.eid.web.dto.request.detainee.QueryDetaineeRequest;
import vn.mk.eid.web.dto.response.DetaineeResponse;

public interface DetaineeRepositoryCustom {
    Page<DetaineeResponse> getWithPaging(QueryDetaineeRequest request, Pageable pageable);
}
