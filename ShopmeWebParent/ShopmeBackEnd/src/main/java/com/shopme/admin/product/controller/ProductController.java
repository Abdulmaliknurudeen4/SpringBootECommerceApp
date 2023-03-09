package com.shopme.admin.product.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.product.ProductService;
import com.shopme.entity.Brand;
import com.shopme.entity.Product;
import com.shopme.entity.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
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
                              @RequestParam(value = "detailValues", required = false) String[] detailValues,
                              @RequestParam(value = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(value = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(value = "imageNames", required = false) String[] imageNames) throws IOException {
        setMainImageName(photoMultipart, product);
        setExistingExtraImageNames(imageIDs, imageNames, product);
        setNewExtraImageNames(extraImageMultiparts, product);
        setProductDetails(detailIDs,detialsNames, detailValues, product);
        Product savedProduct = productService.save(product);
        saveUploadedImages(photoMultipart, extraImageMultiparts, savedProduct);

        deleteExtraImagesWereRemovedOnForm(product);

        ra.addFlashAttribute("message", "The product has been saved Succesfully.");
        return "redirect:/products";
    }

    private void deleteExtraImagesWereRemovedOnForm(Product product) {
        String extraImage = "/product-images/" + product.getId() + "/extras";
        Path dirpaths = Paths.get(extraImage);

        try {
            Files.list(dirpaths).forEach(file -> {
                String filename = file.toFile().getName();
                if (!product.containsImageName(filename)) {
                    try {
                        Files.delete(file);
                        LOGGER.info("Deleted extra images " + filename);
                    } catch (IOException e) {
                        LOGGER.error("could not delete extra image" + filename);
                    }
                }
            });
        } catch (IOException ignored) {
            LOGGER.error("Could not list directory " + dirpaths);
        }
    }

    private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;
        if (imageNames == null || imageNames.length == 0) return;

        Set<ProductImage> images = new HashSet<>();
        for (int count = 0; count < imageNames.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];
            images.add(new ProductImage(id, name, product));
        }
        product.setImages(images);
    }

    private void setProductDetails(String[] detailIDs, String[] detialsNames, String[] detailValues, Product product) {
        if (detialsNames == null || detialsNames.length == 0) return;
        if (detailValues == null || detailValues.length == 0) return;

        for (int i = 0; i < detialsNames.length; i++) {
            String name = detialsNames[i];
            String value = detailValues[i];

            Integer id = Integer.valueOf(detailIDs[i]);
            if(id != 0){
                product.addDetails(id, name, value);
            }else if (!name.isEmpty() && !value.isEmpty()) {
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

    private void setNewExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
        if (extraImageMultipart.length > 0) {
            Arrays.stream(extraImageMultipart).forEach(multipartFile -> {
                String name = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                if (!product.containsImageName(name)) {
                    product.addExtraImage(name);
                }
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
