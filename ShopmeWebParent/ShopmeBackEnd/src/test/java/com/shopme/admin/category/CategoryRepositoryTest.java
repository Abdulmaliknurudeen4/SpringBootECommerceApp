package com.shopme.admin.category;

import com.shopme.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repo;
    @Autowired
    private TestEntityManager entity;

    @Test
    public void testCreateRootCategory(){
        Category category =
                new Category("Electronics");
      Category savedCategory = repo.save(category);
      assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateSubCategories(){
        Category ParentComputer = entity.find(Category.class, 1);
        Category ParentElectronics = entity.find(Category.class, 2);

        List<Category> computerChildren = new ArrayList<>();
        computerChildren.add(new Category("Desktop"));
        computerChildren.add(new Category("Laptops"));
        computerChildren.add(new Category("Computer components"));

        List<Category> electronicsChildren = new ArrayList<>();
        electronicsChildren.add(new Category("Cameras"));
        electronicsChildren.add(new Category("Smartphones"));

        computerChildren.stream().forEach(category -> {
            category.setParent(ParentComputer);
        });

        electronicsChildren.stream().forEach(category -> {
            category.setParent(ParentElectronics);
        });

       Iterable<Category> saved = repo.saveAll(electronicsChildren);
       assertThat(saved).isNotEmpty();
    }

    @Test
    public void testCreateSubCategoriesLeveled(){
        Category cc = entity.find(Category.class, 5);
        Category child = new Category("Memory");
        child.setParent(cc);
        assertThat(repo.save(child).getId()).isNotEqualTo(0);

    }

    @Test
    public void printCategory(){
        // We'll need some method to find only parent categories
        Iterable<Category> parentCategories = repo.getParentCategories();
        parentCategories.forEach(category -> {
            System.out.println(category.getName());
            if(!category.getChildren().isEmpty()){
                category.getChildren().forEach(subcate->{
                    System.out.println("\t" + subcate.getName());
                });
            }
        });
        assertThat(parentCategories.iterator()).isNotNull();
    }
    
    @Test
    public void testPrintHierachicalCategories(){
        Iterable<Category> categories = repo.findAll();
        for(Category category: categories){
            if(category.getParent() == null){
                System.out.println(category.getName());
                Set<Category> children = category.getChildren();
                
                for(Category subCategory: children){
                    System.out.println("=="+subCategory.getName());
                    printChildren(subCategory, 1);
                }
                
            }
        }

        assertThat(0).isEqualTo(0);
    }

    private void printChildren(Category parent, int sublevel) {
        int newSubLevel = sublevel + 1;
        Set<Category> children = parent.getChildren();

        for(Category subCategory: children){
            for (int i = 0; i < newSubLevel; i++) {
                System.out.print("==");
            }
            System.out.println(subCategory.getName());
            printChildren(subCategory, newSubLevel);
        }

    }

    @Test
    public void testListRootCategories(){
        List<Category> rootCategories = repo.findRootCategories(Sort.by("name"));
        rootCategories.forEach(cat -> System.out.println(cat.getName()));
    }

    @Test
    public void testFindByName(){
        String name = "Computers";
        Category category = repo.findByName(name);
        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(name);
    }


    @Test
    public void testFindByAlias(){
        String alias = "computers";
        Category category = repo.findByAlias(alias);
        assertThat(category).isNotNull();
        assertThat(category.getAlias()).isEqualTo(alias);
    }

}
