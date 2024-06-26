package com.shopme;

import com.shopme.category.CategoryService;
import com.shopme.entity.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private CategoryService categoryService;


    @GetMapping("")
    public String viewHomePage(Model model) {
        List<Category> categoryList = categoryService.listNoChildrenCategories();
        model.addAttribute("categories", categoryList);
        return "index";
    }

    @GetMapping("/")
    public String viewHomePage2(Model model) {
        List<Category> categoryList = categoryService.listNoChildrenCategories();
        model.addAttribute("categories", categoryList);
        return "index";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

}
