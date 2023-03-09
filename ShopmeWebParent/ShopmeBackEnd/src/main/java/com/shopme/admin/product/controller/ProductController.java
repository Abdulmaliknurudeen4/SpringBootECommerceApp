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
import java.util.Arrays;
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
        model.addAttribute("numberOfExistingExtraImages", 0);
        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrand);
        model.addAttribute("pageTitle", "Create New Product");

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product,
                              RedirectAttributes ra,
                              @RequestParam(name = "image") MultipartFile photoMultipart,
                              @RequestParam(name = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
                              @RequestParam(value = "detailNames", required = false) String[] detialsNames,
                              @RequestParam(value = "detailValues", required = false) String[] detailValues) throws IOException {
        setMainImageName(photoMultipart, product);
        setExtraImageNames(extraImageMultiparts, product);
        setProductDetails(detialsNames, detailValues, product);
        Product savedProduct = productService.save(product);
        saveUploadedImages(photoMultipart, extraImageMultiparts, savedProduct);

        ra.addFlashAttribute("message", "The product has been saved Succesfully.");
        return "redirect:/products";
    }

    private void setProductDetails(String[] detialsNames, String[] detailValues, Product product) {
        if (detialsNames == null || detialsNames.length == 0) return;
        if (detailValues == null || detailValues.length == 0) return;

        for (int i = 0; i < detialsNames.length; i++) {
            String name = detialsNames[i];
            String value = detailValues[i];
            if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetails(name, value);
            }
        }

    }

    private void setMainImageName(MultipartFile mainImageMultipart, Product product) {

        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
            product.setMainImage(fileName);
        }
    }

    private void setExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
        if (extraImageMultipart.length > 0) {
            Arrays.stream(extraImageMultipart).forEach(multipartFile -> {
                product.addExtraImage(StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())));
            });
        }
    }

    private void saveUploadedImages(MultipartFile main, MultipartFile[] extras, Product product) throws IOException {
        if (!main.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(main.getOriginalFilename()));
            String uploadDir = "product-images/" + product.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, main);
        }
        if (extras.length > 0) {
            String uploadDir = "product-images/" + product.getId() + "/extras";
            for (MultipartFile extra : extras) {
                if (extra.isEmpty()) continue;
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(extra.getOriginalFilename()));
                FileUploadUtil.saveFile(uploadDir, fileName, extra);

            }
        }
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
            String productImageDir = "product-images/" + id + "/extras";
            String productImageDirParent = "product-images/" + id;
            FileUploadUtil.removeDir(productImageDir);
            FileUploadUtil.removeDir(productImageDirParent);
            redirectAttributes.addFlashAttribute("message",
                    "The User with the ID : " + id + " has been deleted Successfully!");
        } catch (ProductNotFoundException e) {
            // TODO Auto-generated catch block
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Product product = productService.getProduct(id);
            Integer numberOfExistingExtraImages = product.getImages().size();
            Integer numberOfExistingProductDetails = product.getDetails().size();
            List<Brand> listBrands = brandService.listAll();
            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + " )");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
            model.addAttribute("numberOfExistingProductDetails", numberOfExistingProductDetails);
            return "products/product_form";
        } catch (ProductNotFoundException e) {
            e.printStackTrace();
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }

    }
}
