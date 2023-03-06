package com.shopme.admin.product.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.product.ProductService;
import com.shopme.entity.Brand;
import com.shopme.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        List<Product> productList = productService.listAll();
        model.addAttribute("listProducts", productList);
        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
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
    public String saveProduct(Product product, RedirectAttributes ra, @RequestParam(name = "image") MultipartFile photoMultipart) throws IOException {


        if (!photoMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(photoMultipart.getOriginalFilename()));
            product.setMainImage(fileName);
            System.out.println(product.getMainImagePath());
            Product savedProduct = productService.save(product);
            String uploadDir = "product-images/" + savedProduct.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, photoMultipart);

        } else {
            if (product.getMainImage().isEmpty())
                product.setMainImage(null);
            productService.save(product);
        }

        ra.addFlashAttribute("message", "The product has been saved Succesfully.");
        return "redirect:/products";
    }

    @GetMapping("/products/setProductStatus/{id}/{status}")
    public String changeProductEnableStatus(@PathVariable(name = "id") Integer id,
                                            @PathVariable(name = "status") boolean status,
                                            RedirectAttributes redirectAttributes) {
        String enabled = (status) ? "enabled" : "disabled";
        try {
            productService.setEnableProduct(status, id);
            redirectAttributes.addFlashAttribute("message",
                    "The Product with the ID : " + id + " has been " + enabled + " Successfully!");
        } catch (ProductNotFoundException e) {
            // TODO Auto-generated catch block
            redirectAttributes.addFlashAttribute("message",
                    "The Product with the ID : " + id + " has been " + enabled + " Successfully!");
        }
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message",
                    "The User with the ID : " + id + " has been deleted Successfully!");
        } catch (ProductNotFoundException e) {
            // TODO Auto-generated catch block
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/products";
    }

}
