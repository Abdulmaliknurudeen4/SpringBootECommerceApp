package com.shopme.admin.category;

import com.shopme.entity.Category;
import com.shopme.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends CrudRepository<Category, Integer>, PagingAndSortingRepository<Category, Integer> {
    @Query("SELECT c from Category c WHERE c.parent IS NULL")
    public Iterable<Category> getParentCategories();
}
