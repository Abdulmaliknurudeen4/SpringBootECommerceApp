package com.shopme.category;

import com.shopme.entity.Category;
import com.shopme.exception.CategoryNotFoundException;
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

    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category byAliasEnabled = categoryRepository.findByAliasEnabled(alias);
        if (byAliasEnabled == null) {
            throw new CategoryNotFoundException("Could not find Category with Alias "+alias);
        }
        return byAliasEnabled;
    }

    public List<Category> getCategoryParents(Category child) {
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
