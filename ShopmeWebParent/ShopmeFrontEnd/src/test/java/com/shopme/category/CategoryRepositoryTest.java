package com.shopme.category;

import com.shopme.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void printAllEnabled() {
        categoryRepository.findAllEnabled().stream().forEach((Consumer<? super Category>) System.out::println);
        assertThat(true).isTrue();
    }

    @Test
    public void testGetAllEnabeledCategory() {
        assertThat(categoryRepository.findAllEnabled()).isNotEmpty().allMatch(Category::isEnabled);
    }

    @Test
    public void testCategoryByAlias() {
        String alias = "electronics";
        Category byAliasEnabled = categoryRepository.findByAliasEnabled(alias);
        assertThat(byAliasEnabled).isNotNull();
    }
}
