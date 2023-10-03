package com.shopme.admin.brand.controller;

import com.shopme.admin.brand.BrandService;
import com.shopme.entity.Brand;
import com.shopme.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class BrandRestController {

    @Autowired
    private BrandService brandService;
    @PostMapping("brands/check_brand")
    public String checkDuplicateBrandName(String name, Integer id) {
        return brandService.isNameUnique(name, id) ? "OK" : "Duplicated";
    }

    @GetMapping("brands/listCategoriesByBrand/{id}")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer id) throws BrandNotFoundRestException {
        List<CategoryDTO> listCategories;
        try {
            Brand brand = brandService.getBrand(id);
            Set<Category> categories = brand.getCategories();
            listCategories = categories.stream().map(CategoryDTO::new).collect(Collectors.toList());
            return listCategories;
        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundRestException();
        }
    }
}
