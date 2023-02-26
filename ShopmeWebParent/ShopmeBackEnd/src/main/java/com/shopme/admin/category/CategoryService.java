package com.shopme.admin.category;

import com.shopme.admin.category.controller.CategoryNotFoundException;
import com.shopme.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional
public class CategoryService {

    public static final int CATEGORIES_PER_PAGE = 8;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listAll() {
        return (List<Category>) categoryRepository.findAll(Sort.by("name").ascending());
    }

    public Page<Category> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE, sort);
        if (keyword != null) {
            return categoryRepository.findAll(keyword, pageable);
        }
        return categoryRepository.findAll(pageable);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    // Under Review
    public boolean isCategoryUnique(Integer id, String categoryName) {
        boolean isCreatingNew = (id == null);
        Category categoryByName = categoryRepository.getCategoriesByName(categoryName);
        if (categoryByName == null)
            return true;
        if (isCreatingNew) {
            return false;
        } else {
            return categoryByName.getId() == id;
        }
    }

    public Category getCategory(Integer id) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new CategoryNotFoundException("could not find any user with the id" + id);
        }
    }

    public void deleteCategory(Integer id) throws CategoryNotFoundException {
        Long categoryCount = categoryRepository.countById(id);
        if (categoryCount == null || categoryCount == 0) {
            throw new CategoryNotFoundException("Category Not Present");
        }
        categoryRepository.deleteById(id);
    }

    public void setEnableCategory(boolean status, Integer id) throws CategoryNotFoundException {
        Long categoryCount = categoryRepository.countById(id);
        if (categoryCount == null || categoryCount == 0) {
            throw new CategoryNotFoundException("Category Not Present");
        }
        categoryRepository.EnableStatusCategory(id, status);
    }

    public Category getCategory(String name) {
        return categoryRepository.getCategoriesByName(name);
    }

    public List<Category> listCategoriesUsedInForm(){
       List<Category> categoriesUsedInForm = new ArrayList<>();
       Iterable<Category> categoriesInDB = categoryRepository.findAll();
        for (Category cat : categoriesInDB) {
            if(cat.getParent()==null){
                categoriesUsedInForm.add(new Category(cat.getName()));

                Set<Category> children = cat.getChildren();
                for(Category subCategory: children){
                    String name = "--"+subCategory.getName();
                    categoriesUsedInForm.add(new Category(name));

                    listChildren(categoriesUsedInForm,subCategory, 1);
                }
            }
        }

        return categoriesUsedInForm;

    }

    private void listChildren(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
        int newSubLevel = subLevel+1;
        Set<Category> children = parent.getChildren();

        for(Category subCategory: children){
            String name = "";
            for(int i = 0; i < newSubLevel; i++){
                name+="--";
            }
            name +=subCategory.getName();
            categoriesUsedInForm.add(new Category(name));
            listChildren(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }
}