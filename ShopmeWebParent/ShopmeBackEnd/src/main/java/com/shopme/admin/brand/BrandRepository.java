package com.shopme.admin.brand;

import com.shopme.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrandRepository extends CrudRepository<Brand, Integer>, PagingAndSortingRepository<Brand, Integer> {
    @Query("SELECT b from Brand b WHERE b.name = :name")
    Brand getBrandByName(@Param("name") String name);

    Long countById(Integer Id);
    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);

    @Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER BY b.name ASC")
    public List<Brand> findAll();


}
