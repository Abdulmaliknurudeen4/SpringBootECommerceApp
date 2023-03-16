package com.shopme;

import com.shopme.category.CategoryService;
import com.shopme.entity.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String index(Model model) {
        List<Category> categoryList = categoryService.listNoChildrenCategories();
        System.out.println(categoryList.size());
        model.addAttribute("categories", categoryList);
        return "index";
    }

}
