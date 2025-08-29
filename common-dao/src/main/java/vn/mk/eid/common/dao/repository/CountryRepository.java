package vn.mk.eid.common.dao.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.CountryEntity;

import java.util.Optional;

/**
 * @author mk
 * @date 06-Aug-2025
 */

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
    Optional<CountryEntity> findByAlpha2Code(String alpha2Code);

    Optional<CountryEntity> findByAlpha3Code(String alpha3Code);

    Optional<CountryEntity> findByCountryName(String name);
}
