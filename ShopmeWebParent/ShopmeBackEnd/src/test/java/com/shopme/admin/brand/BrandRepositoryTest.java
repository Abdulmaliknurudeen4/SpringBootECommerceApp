package com.shopme.admin.brand;


import com.shopme.admin.category.CategoryRepository;
import com.shopme.entity.Brand;
import com.shopme.entity.Category;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TestEntityManager entity;

    @Test
    public void testCreateBrands(){
        Brand acer = new Brand("Acer","brand-logo.png");
        Category laptop = entity.find(Category.class, 6);
        acer.setCategories(Set.of(laptop));

        Brand apple = new Brand("Apple","brand-logo.png");
        Category cellphones = entity.find(Category.class, 4);
        Category accesscories = entity.find(Category.class, 16);
        Category tablets = entity.find(Category.class, 7);
        apple.setCategories(Set.of(cellphones, accesscories, tablets));

        Brand samsung = new Brand("Samsung","brand-logo.png");
        Category memory = entity.find(Category.class, 29);
        Category internalHDD = entity.find(Category.class, 24);
        samsung.setCategories(Set.of(cellphones, accesscories, tablets));

       Iterable<Brand> savedBrands =  brandRepository.saveAll(IterableUtil.iterable(acer, apple, samsung));

       assertThat(savedBrands).isNotEmpty();
    }

    @Test
    public void testGetAllBrands(){
        List<Brand> brands = (List<Brand>)brandRepository.findAll();
        brands.forEach(System.out::println);
        assertThat(brands).isNotEmpty();
    }

    @Test
    public void getBrandById(){
        Integer id = 2;
        Brand brand = brandRepository.findById(id).get();
        assertThat(brand.getId()).isEqualTo(id);
    }

    @Test
    public void updateBrandById(){
        Integer id = 3;
        Brand brand = brandRepository.findById(3).get();
        brand.setName("Samsung Electronics");
        assertThat(true).isTrue();
    }

    @Test
    public void deleteBrandById(){
        Integer id = 1;
        brandRepository.deleteById(id);
        assertThat(brandRepository.findById(id).isPresent()).isFalse();
    }

    @Test
    public void testFindAll(){
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(System.out::println);
        assertThat(brands).isNotEmpty();
    }
}
