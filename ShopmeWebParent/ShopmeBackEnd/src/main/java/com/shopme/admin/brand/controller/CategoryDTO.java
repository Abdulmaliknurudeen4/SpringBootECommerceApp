package com.shopme.admin.brand.controller;

import com.shopme.entity.Category;

public class CategoryDTO {
    private Integer id;
    private String name;

    public CategoryDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public CategoryDTO(Category category){
        this.id = category.getId();
        this.name = category.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
