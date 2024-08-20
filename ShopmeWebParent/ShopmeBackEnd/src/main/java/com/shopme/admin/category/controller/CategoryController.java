package com.shopme.admin.category.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryCSVExporter;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.entity.Category;
import com.shopme.exception.CategoryNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
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
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Controller()
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    private static String getRedirectURLToAffectedCategory(Category category) {
        String keyPart = category.getName();
        return "redirect:/categories/page/1?sortField=name&sortDir=asc&keyword=" + keyPart;
    }

    @GetMapping("/categories")
    public String listFirstPage() {
        return "redirect:/categories/page/1?sortField=name&sortDir=asc&keyword=";
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "listCategories", moduleURL = "/categories", contextDisplay = "categories") PagingAndSortingHelper helper,
                             @PathVariable(name = "pageNum") int pageNum) {

        categoryService.listByPage(pageNum, helper);
        return "category/categories";
    }

    @GetMapping("/categories/setCategoryStatus/{id}/{status}")
    public String changeCategoryStatus(@PathVariable(name = "id") Integer id,
                                       @PathVariable(name = "status") boolean status,
                                       RedirectAttributes redirectAttributes) {
        String enabled = (status) ? "enabled" : "disabled";
        try {
            categoryService.setEnableCategory(status, id);
            redirectAttributes.addFlashAttribute("message",
                    "The Category with the ID : " + id + " has been " + enabled + " Successfully!");
        } catch (CategoryNotFoundException e) {
            // TODO Auto-generated catch block
            redirectAttributes.addFlashAttribute("message",
                    "The Category with the ID : " + id + " has been " + enabled + " Successfully!");
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public String exportToCSV(HttpServletResponse response) throws IOException {
        List<Category> categoryList = categoryService.listAll("name");
        CategoryCSVExporter exporter = new CategoryCSVExporter();
        exporter.export(categoryList, response);
        return "redirect:/categories";
    }

    @GetMapping("/categories/new")
    public String newUser(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        Category category = new Category();
        category.setEnabled(true);
        model.addAttribute("editMode", false);
        model.addAttribute("category", category);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");
        model.addAttribute("moduleURL", "categories");
        return "category/category_form";
    }

    @PostMapping("/categories/save")
    public String saveUser(Category category, RedirectAttributes redirectAttributes, Model model,
                           @RequestParam(name = "image") MultipartFile photoMultipart) throws IOException {
        if (!photoMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(photoMultipart.getOriginalFilename()));
            category.setPhoto(fileName);
            System.out.println(category.getPhoto());
            Category savedCategory = categoryService.save(category);
            String uploadDir = "../categories-images/" + savedCategory.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, photoMultipart);

            /* //Amazon s3
            String photoDir = "user-photos/"+id;
            AmazonS3Util.removeFolder(photoDir);*/

        } else {
            if (category.getPhoto().isEmpty())
                category.setPhoto(null);
            categoryService.save(category);
        }

        // Review
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully. !");
        return getRedirectURLToAffectedCategory(category);
    }

    @GetMapping("/categories/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {

        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("editMode", true);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Edit Category (ID: " + id + " )");
        model.addAttribute("moduleURL", "/categories");
        model.addAttribute("contextDisplay", "categor(ies)");
        try {
            Category editCategory = categoryService.getCategory(id);
            model.addAttribute("category", editCategory);
            return "category/category_form";
        } catch (CategoryNotFoundException e) {
            // Review
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/categories";
        }

    }

    @GetMapping("/categories/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            String dir = "../categories-images/"+id;
            categoryService.deleteCategory(id);
            FileUploadUtil.removeDir(dir);
            redirectAttributes.addFlashAttribute("message",
                    "The Category with the ID : " + id + " has been deleted Successfully!");
        } catch (CategoryNotFoundException e) {
            // TODO Auto-generated catch block
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/categories";
    }
}