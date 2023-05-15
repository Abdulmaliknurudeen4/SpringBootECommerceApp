package com.shopme.admin.product.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.product.ProductSaveHelper;
import com.shopme.admin.product.ProductService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.entity.Brand;
import com.shopme.entity.Category;
import com.shopme.entity.Product;
import com.shopme.exception.ProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String listFirstPage() {
        return "redirect:/products/page/1?sortField=name&sortDir=asc&keyword=";
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "listProducts", moduleURL = "/products", contextDisplay = "product(s)") PagingAndSortingHelper helper,
                             @PathVariable(name = "pageNum") int pageNum,
                             @Param("categoryId") Integer categoryId, Model model) {
        List<Category> categoryList = categoryService.listCategoriesUsedInForm();
        productService.listByPage(pageNum, helper, categoryId);
        model.addAttribute("listCategories", categoryList);
        if (categoryId != null) {
            model.addAttribute("categoryId", categoryId);
        }
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
                              @RequestParam(name = "image", required = false) MultipartFile photoMultipart,
                              @RequestParam(name = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
                              @RequestParam(value = "detailNames", required = false) String[] detialsNames,
                              @RequestParam(value = "detailValues", required = false) String[] detailValues,
                              @RequestParam(value = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(value = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(value = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {
        //For SalesPerson Permission to Update Costs only.
        //Bugged Code
        Boolean islogged = loggedUser.hasRole("Salesperson");
        if (loggedUser.hasRole("Salesperson") && !loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
            productService.saveProductPrice(product);
            ra.addFlashAttribute("message", "The product has been Updated Succesfully.");
            return "redirect:/products";
        } else {
            // For Admin, Editor
            // trim the multipart for the extra files
            List<MultipartFile> filtered = null;
            if (extraImageMultiparts != null) {
                filtered = Arrays.stream(extraImageMultiparts).filter(multipartFile -> !multipartFile.isEmpty()).toList();
            }
            ProductSaveHelper.setMainImageName(photoMultipart, product);
            ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
            ProductSaveHelper.setNewExtraImageNames(filtered, product);
            ProductSaveHelper.setProductDetails(detailIDs, detialsNames, detailValues, product);
            Product savedProduct = productService.save(product);
            ProductSaveHelper.saveUploadedImages(photoMultipart, filtered, savedProduct);

            ProductSaveHelper.deleteExtraImagesWereRemovedOnForm(product);

            ra.addFlashAttribute("message", "The product has been saved Succesfully.");
            return "redirect:/products";
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
            String productImageDir = "../product-images/" + id + "/extras";
            String productImageDirParent = "../product-images/" + id;
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

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Product product = productService.getProduct(id);
            model.addAttribute("product", product);

            return "products/product_detail_modal";
        } catch (ProductNotFoundException e) {
            e.printStackTrace();
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }

    }
}
