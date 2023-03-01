package com.shopme.admin.brand;

import com.shopme.entity.Brand;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface BrandRepository extends CrudRepository<Brand, Integer>, PagingAndSortingRepository<Brand, Integer> {
    @Query("SELECT b from Brand b WHERE b.name = :name")
    Brand getBrandByName(@Param("name") String name);

    Long countById(Integer Id);
}
