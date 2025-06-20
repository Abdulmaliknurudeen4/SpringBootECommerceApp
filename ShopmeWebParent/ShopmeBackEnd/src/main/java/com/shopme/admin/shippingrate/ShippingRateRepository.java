package com.shopme.admin.shippingrate;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShippingRateRepository extends SearchRepository<ShippingRate, Integer> {

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.Id = ?1 AND sr.state = ?2")
    ShippingRate findByCountryAndState(Integer countryId, String State);

    @Query("UPDATE ShippingRate sr SET sr.codSupported = ?2 WHERE sr.Id = ?1")
    @Modifying
    void updateCODSupport(Integer id, boolean enabled);

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.name LIKE %?1% OR sr.state LIKE %?1%")
    Page<ShippingRate> findAll(String keyword, Pageable pageable);

    Long countById(Integer id);
}
