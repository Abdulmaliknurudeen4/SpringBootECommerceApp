package com.shopme.admin.product.controller;

import com.shopme.admin.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @PostMapping("products/check_name")
    public String checkDuplicateCategory(@Param("name") String name, @Param("id") Integer id) {
        return productService.isProductUnique(name, id) ? "OK" : "Duplicated";
    }
}
