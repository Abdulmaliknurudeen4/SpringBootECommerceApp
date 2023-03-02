package com.shopme.admin.brand.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.user.UserNotFoundExcpetion;
import com.shopme.admin.user.UserService;
import com.shopme.entity.Brand;
import com.shopme.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/brands")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "name", "asc", null);
    }
    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
        System.out.println("Sort Field " + sortField);
        System.out.println("Sort Order " + sortDir);
        System.out.println("Keyword " + keyword);

        Page<Brand> page = brandService.listByPage(pageNum, sortField, sortDir, keyword);
        List<Brand> brandList = page.getContent();
        pageNum = (pageNum <= 0) ? 0 : pageNum;

        long startCount = (long) (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
        long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;

        // Getting to the last page with uneven elements
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("brands", brandList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        return "brand/brands";

    }

    @GetMapping("/brands/new")
    public String newBrand(Model model) {

        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        Brand brand = new Brand();
        model.addAttribute("editMode", false);
        model.addAttribute("brand", brand);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Brand");
        return "brand/brand_form";
    }


    @PostMapping("brands/save")
    public String saveBrand(Brand brand, RedirectAttributes redirectAttributes, Model model,
                            @RequestParam(name = "image") MultipartFile photoMultipart) throws IOException {
        if (!photoMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(photoMultipart.getOriginalFilename()));
            brand.setLogo(fileName);
            System.out.println(brand.getLogo());
            Brand savedBrand = brandService.save(brand);
            String uploadDir = "brand-photos/" + savedBrand.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, photoMultipart);

        } else {
            if (brand.getLogo().isEmpty())
                brand.setLogo(null);
            brandService.save(brand);
        }
        // Review
        redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully. !");
        return getRedirectURLToAffectedBrand(brand);
    }

    private static String getRedirectURLToAffectedBrand(Brand brand) {
//        String keyPart = brand.getName();
//        return "redirect:/brands/page/1?sortField=name&sortDir=asc&keyword=" + keyPart;
        return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("pageTitle", "Edit Brand (ID: " + id + " )");
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("editMode", true);
        try {
            Brand brand = brandService.getBrand(id);
            model.addAttribute("brand", brand);
            return "brand/brand_form";
        } catch (BrandNotFoundException e) {
            // Review
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/brands";
        }

    }

    @GetMapping("/brands/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            brandService.deleteBrand(id);
            redirectAttributes.addFlashAttribute("message",
                    "The Brand with the ID : " + id + " has been deleted Successfully!");
        } catch (BrandNotFoundException e) {
            // TODO Auto-generated catch block
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/brands";
    }


}
