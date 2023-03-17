package com.shopme.category;

import com.shopme.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listNoChildrenCategories() {
        List<Category> listEnabledCategories = categoryRepository.findAllEnabled();
        return listEnabledCategories.stream().filter(c -> !c.hasChildren()).toList();
    }

    public Category getCategory(String alias) {
        return categoryRepository.findByAliasEnabled(alias);
    }

    public List<Category> getCartegoryParents(Category child) {
        //rewrite Using Clearer Logic and Set.
        List<Category> listparent = new ArrayList<>();

        Category parent = child.getParent();
        while (parent != null) {
            listparent.add(0, parent);
            parent = parent.getParent();
        }
        listparent.add(child);
        return listparent;
    }

}
