package com.shopme.admin.brand;

import com.shopme.entity.Brand;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
}
