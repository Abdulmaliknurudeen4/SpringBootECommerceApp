package com.shopme.category;

import com.shopme.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

<<<<<<< HEAD
    public List<Category> listNoChildrenCategories() {
        List<Category> listEnabledCategories = categoryRepository.findAllEnabled();
        return listEnabledCategories.stream().filter(Category::hasChildren).toList();
=======
    public List<Category> listNoChildrenCategories(){
        List<Category> listEnabledCategories = categoryRepository.findAllEnabled();
//        return listEnabledCategories.stream().filter(Category::hasChildren).toList();
        return listEnabledCategories;
>>>>>>> 8ff85b34710eac20d129a7eceb6b404d39290ae1
    }
}
