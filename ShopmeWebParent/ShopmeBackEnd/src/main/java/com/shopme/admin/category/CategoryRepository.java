package com.shopme.admin.category;

import com.shopme.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Integer>, PagingAndSortingRepository<Category, Integer> {
    @Query("SELECT c from Category c WHERE c.parent IS NULL")
    Iterable<Category> getParentCategories();

    Long countById(Integer Id);

    @Query("SELECT c FROM Category c WHERE CONCAT(c.name, ' ', c.alias) LIKE %?1%")
    Page<Category> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
    @Modifying
    void EnableStatusCategory(Integer id, boolean status);

    @Query("SELECT c from Category c WHERE c.name = :name")
    Category getCategoriesByName(@Param("name") String name);

    Category findByName(String name);
    Category findByAlias(String alias);

    @Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
    List<Category> findRootCategories();
}
