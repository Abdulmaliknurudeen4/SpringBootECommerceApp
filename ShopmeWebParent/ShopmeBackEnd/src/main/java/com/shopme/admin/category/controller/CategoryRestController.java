package com.shopme.admin.category.controller;

import com.shopme.admin.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {

    @Autowired
    private CategoryService service;

    @PostMapping("categories/check_name")
    public String checkDuplicateCategory(@Param("name") String name, @Param("alias") String alias, @Param("id") Integer id) {
        return service.isCategoryUnique(id, name, alias);
    }
}
