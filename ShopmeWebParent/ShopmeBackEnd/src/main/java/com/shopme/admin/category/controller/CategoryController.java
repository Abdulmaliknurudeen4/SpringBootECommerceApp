package com.shopme.admin.category.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryCSVExporter;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.user.export.UserCSVExporter;
import com.shopme.entity.Category;
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
import java.util.List;
import java.util.Objects;

@Controller()
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "name", "asc", null);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){

        Page<Category> page = categoryService.listByPage(pageNum, sortField, sortDir, keyword);
        List<Category> listCategories = page.getContent();
        pageNum = (pageNum <= 0) ? 0 : pageNum;

        long startCount = (long) (pageNum - 1) * CategoryService.CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.CATEGORIES_PER_PAGE - 1;

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
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
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
        List<Category> categoryList = categoryService.listAll();
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
            String uploadDir = "categories-images/" + savedCategory.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, photoMultipart);

        } else {
            if (category.getPhoto().isEmpty())
                category.setPhoto(null);
            categoryService.save(category);
        }

        // Review
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully. !");
        return getRedirectURLToAffectedCategory(category);
    }

    private static String getRedirectURLToAffectedCategory(Category category) {
        String keyPart = category.getName();
        return "redirect:/categories/page/1?sortField=name&sortDir=asc&keyword=" + keyPart;
    }
}
/*

@Controller
public class UserController {

    @GetMapping("/users/new")
    public String newUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("editMode", false);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", service.listRoles());
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, Model model,
                           @RequestParam(name = "image") MultipartFile photoMultipart) throws IOException {
        if (!photoMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(photoMultipart.getOriginalFilename());
            user.setPhotos(fileName);
            System.out.println(user.getPhotos());
            User savedUser = service.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, photoMultipart);

        } else {
            if (user.getPhotos().isEmpty())
                user.setPhotos(null);
            service.save(user);
        }

        // Review
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully. !");
        return getRedirectURLToAffectedUser(user);
    }

    private static String getRedirectURLToAffectedUser(User user) {
        String keyPart = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + keyPart;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("pageTitle", "Edit User (ID: " + id + " )");
        model.addAttribute("listRoles", service.listRoles());
        model.addAttribute("editMode", true);
        try {
            User user = service.getUser(id);
            model.addAttribute("user", user);
            return "users/user_form";
        } catch (UserNotFoundExcpetion e) {
            // Review
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }

    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteUser(id);
            redirectAttributes.addFlashAttribute("message",
                    "The User with the ID : " + id + " has been deleted Successfully!");
        } catch (UserNotFoundExcpetion e) {
            // TODO Auto-generated catch block
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }



}
*/
