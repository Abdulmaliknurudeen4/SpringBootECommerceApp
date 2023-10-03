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
    public String checkDuplicateCategory(String name, String alias, Integer id) {
        System.out.println(service.isCategoryUnique(id, name, alias));
        return service.isCategoryUnique(id, name, alias);
    }
}
