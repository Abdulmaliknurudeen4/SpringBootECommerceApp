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

import java.util.*;

@Service
@Transactional
public class CategoryService {

    public static final int CATEGORIES_PER_PAGE = 8;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listAll(String sortDir) {

//        return (List<Category>) categoryRepository.findAll(Sort.by("name").ascending());
        Sort sort = Sort.by("name");

        if(sortDir == null || sortDir.isEmpty()){
            sort = sort.ascending();
        }else if (sortDir.equals("asc")){
            sort = sort.ascending();
        }else if(sortDir.equals("desc")){
            sort = sort.descending();
        }

        List<Category> rootCategories = categoryRepository.findRootCategories(Sort.by("name").ascending());
        return listHeirarchicalCategories(rootCategories, sortDir);
    }
    private List<Category> listHeirarchicalCategories(List<Category> rootCategories, String sortDir){
        List<Category> heirarchicalCategories = new ArrayList<>();
        for(Category rootCategory: rootCategories){
            heirarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

            for(Category subCategory: children){
                String name = "--"+subCategory.getName();
                heirarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHeirarchicalCategories(heirarchicalCategories, subCategory, 1, sortDir);
            }
        }
        return heirarchicalCategories;
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
    public String isCategoryUnique(Integer id, String categoryName, String alias) {
        boolean isCreatingNew = (id == null || id == 0);
        Category categoryByName = categoryRepository.findByName(categoryName);
        if(isCreatingNew){
            if(categoryByName != null){
                return "DuplicateName";
            }else {
                return (categoryRepository.findByAlias(alias) != null) ? "DuplicateAlias" : "";
            }
        }else{
            if(categoryByName != null && categoryByName.getId() != id)
                return "DuplicateName";

            Category categoryByAlias = categoryRepository.findByAlias(alias);
            if(categoryByAlias != null && categoryByAlias.getId() != id) return "DuplicateAlias";
        }
        return "OK";
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
       Iterable<Category> categoriesInDB = categoryRepository.findRootCategories(Sort.by("name").ascending());
        for (Category cat : categoriesInDB) {
            if(cat.getParent()==null){
                categoriesUsedInForm.add(Category.copyIdAndName(cat));

                Set<Category> children = sortSubCategories(cat.getChildren());
                for(Category subCategory: children){
                    String name = "--"+subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
                    listSubCategoriesUsedInForm(categoriesUsedInForm,subCategory, 1);
                }
            }
        }

        return categoriesUsedInForm;

    }

    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
        int newSubLevel = subLevel+1;
        Set<Category> children = sortSubCategories(parent.getChildren());

        for(Category subCategory: children){
            String name = "";
            for(int i = 0; i < newSubLevel; i++){
                name+="--";
            }
            name +=subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
            listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    private void listSubHeirarchicalCategories(List<Category> heirarchicalCategories,Category parent, int subLevel, String sortDir){
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newSubLevel = subLevel+1;
         for(Category subCategory: children){
             String name = "";
             for(int i = 0; i < newSubLevel; i++){
                 name+="--";
             }
             name +=subCategory.getName();

             heirarchicalCategories.add(Category.copyFull(subCategory, name));

             listSubHeirarchicalCategories(heirarchicalCategories, subCategory, newSubLevel, sortDir);
         }
    }
    private SortedSet<Category> sortSubCategories(Set<Category> children){
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir){
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if(sortDir.equals("asc")){
                    return cat1.getName().compareTo(cat2.getName());
                }else{
                    return cat2.getName().compareTo(cat1.getName());
                }
            }
        });
        sortedChildren.addAll(children);
        return sortedChildren;
    }
}