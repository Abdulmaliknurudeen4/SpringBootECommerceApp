package com.shopme.admin.brand.controller;

import com.shopme.admin.brand.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {

    @Autowired
    private BrandService brandService;
    @PostMapping("brands/check_brand")
    public String checkDuplicateBrandName(@Param("name") String name, @Param("id") Integer id) {
        return brandService.isNameUnique(name, id) ? "OK" : "Duplicated";
    }
}
