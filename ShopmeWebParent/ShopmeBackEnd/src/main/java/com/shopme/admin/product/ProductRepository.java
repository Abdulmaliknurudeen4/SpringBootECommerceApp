package com.shopme.admin.product;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends SearchRepository<Product, Integer> {
    Product findByName(String name);

    Long countById(Integer id);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    void EnableProductStatus(Integer id, boolean status);

    @Query("SELECT p from Product p WHERE p.name LIKE %?1%  " +
           "OR p.shortDescription LIKE %?1% OR p.fullDescription LIKE %?1% " +
           "OR p.brand.name LIKE %?1% OR p.category.name LIKE %?1%")
    Page<Product> findAll(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id =?1 OR p.category.allParentIDs LIKE %?2")
    Page<Product> findAllInCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id =?1 OR p.category.allParentIDs LIKE %?2 AND (p.name LIKE %?3% OR p.shortDescription LIKE %?3% OR p.fullDescription LIKE %?3% OR p.brand.name LIKE %?3% OR p.category.name LIKE %?3%)")
    Page<Product> searchInCategory(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    Page<Product> searchProductByName(String keyword, Pageable pageable);

    @Query("""
            UPDATE Product p SET p.averageRating = CAST(COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1),0) AS float ),
            p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) WHERE p.id = ?1
            """)
    @Modifying
    void updateReviewCountAndAverageRating(Integer productId);

}
