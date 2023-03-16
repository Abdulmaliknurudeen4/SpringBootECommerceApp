package com.shopme;

import com.shopme.category.CategoryService;
import com.shopme.entity.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
=======
>>>>>>> 8ff85b34710eac20d129a7eceb6b404d39290ae1
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

<<<<<<< HEAD
    @Autowired
=======
>>>>>>> 8ff85b34710eac20d129a7eceb6b404d39290ae1
    private CategoryService categoryService;


    @GetMapping("")
<<<<<<< HEAD
    public String index(Model model) {
        List<Category> categoryList = categoryService.listNoChildrenCategories();
        System.out.println(categoryList.size());
=======
    public String viewHomePage(Model model) {
        List<Category> categoryList = categoryService.listNoChildrenCategories();
>>>>>>> 8ff85b34710eac20d129a7eceb6b404d39290ae1
        model.addAttribute("categories", categoryList);
        return "index";
    }

}
