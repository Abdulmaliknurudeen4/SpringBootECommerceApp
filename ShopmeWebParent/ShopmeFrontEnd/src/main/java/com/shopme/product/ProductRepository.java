package com.shopme.product;

import com.shopme.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.enabled = true AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%) ORDER BY p.name ASC")
    public Page<Product> listByCategory(Integer categoryId, String categoryIDMatch, Pageable pageable);

    public Product findByAlias(String alias);

    @Query(value = "SELECT * FROM products WHERE MATCH(name, short_description, full_description) AGAINST (?1) AND enabled = true",
            nativeQuery = true)
    public Page<Product> search(String keyword, Pageable pageable);

    Product findById(Integer id);
}
