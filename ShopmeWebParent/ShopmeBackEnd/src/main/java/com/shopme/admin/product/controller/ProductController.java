package com.shopme.admin.product.controller;

import com.shopme.admin.brand.BrandService;
import com.shopme.admin.product.ProductService;
import com.shopme.entity.Brand;
import com.shopme.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @GetMapping("/products")
    public String listFirstPage(Model model){
        List<Product> productList = productService.listAll();
        model.addAttribute("listProducts", productList);
        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model){
        List<Brand> listBrand = brandService.listAll();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrand);
        model.addAttribute("pageTitle", "Create New Product");
        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product){
        System.out.println("Product Name " + product.getName());
        System.out.println("Brand ID " + product.getBrand().getId());
        System.out.println("Category ID " + product.getCategory().getId());
        return "redirect:/products";
    }
}
