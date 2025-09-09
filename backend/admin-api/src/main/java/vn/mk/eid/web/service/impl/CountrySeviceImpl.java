package vn.mk.eid.web.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.CountryEntity;
import vn.mk.eid.common.dao.repository.CountryRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryWardRequest;
import vn.mk.eid.web.dto.response.CountryResponse;
import vn.mk.eid.web.service.CountrySevice;
import vn.mk.eid.web.specification.CountrySpecification;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mk
 * @date 06-Aug-2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CountrySeviceImpl implements CountrySevice {
    private final CountryRepository countryRepository;

    @Override
    public ServiceResult getAllCountry(QueryWardRequest request) {
        List<CountryResponse> countryResponses = countryRepository
                .findAll(CountrySpecification.getCountrySpecification(request))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ServiceResult.ok(countryResponses);
    }

    private CountryResponse convertToResponse(CountryEntity country) {
        CountryResponse response = new CountryResponse();
        response.setId(country.getId());
        response.setAlpha2Code(country.getAlpha2Code());
        response.setAlpha3Code(country.getAlpha3Code());
        response.setNumericCode(country.getNumericCode());
        response.setName(country.getCountryName());
        return response;
    }
}
