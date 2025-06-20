package com.shopme.admin.product;

import com.shopme.entity.product.Product;

public class ProductDTO {
    private String name;
    private String imagePath;
    private float price;
    private float cost;

    public ProductDTO(String name, String imagePath, float price, float cost) {
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
        this.cost = cost;
    }
    public ProductDTO(Product product){
        setName(product.getName());
        setImagePath(product.getMainImagePath());
        setPrice(product.getDiscountPrice());
        setCost(product.getCost());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
}
